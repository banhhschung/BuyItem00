package com.example.buyitem00.fragment.user

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.buyitem00.R
import com.example.buyitem00.data.UserViewModel
import com.example.buyitem00.data.image.ImageViewModel
import com.example.buyitem00.databinding.FragmentAccountBinding
import com.example.buyitem00.databinding.LayoutDialogBinding
import com.example.buyitem00.model.Image
import com.example.buyitem00.model.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class AccountFragment : Fragment(), OnMapReadyCallback {
    private lateinit var binding: FragmentAccountBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private val imageViewModel: ImageViewModel by activityViewModels()

    private lateinit var dialogView: LayoutDialogBinding
    private lateinit var mMap: GoogleMap
    private var locationProviderClient: FusedLocationProviderClient? = null
    private var currentLocation: Location? = null
    private var currentMarker: Marker? = null
    private lateinit var imageUri: Uri
    private var user = User()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAccountBinding.inflate(layoutInflater)
        userViewModel.readAllData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                user = it[0]
                getInformationUser(user)
            }
        }

        binding.imageAvatar.setOnClickListener {
            selectImage()
        }


        binding.btnSave.setOnClickListener {
            saveData()
        }

        binding.btnLocation.setOnClickListener {
            dialogView = LayoutDialogBinding.inflate(layoutInflater)
            val mBuilder = AlertDialog.Builder(requireContext())
                .setView(dialogView.root)
            val alertDialog = mBuilder.show()
            locationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireContext())
            fetchLocation()
            dialogView.btnYourLocation.setOnClickListener {
                currentMarker?.remove()
                val latLong = LatLng(currentLocation?.latitude!!, currentLocation?.longitude!!)
                drawMarker(latLong)
            }
            alertDialog.setOnDismissListener {
                val fragment = childFragmentManager.findFragmentById(R.id.map)
                if (fragment != null) childFragmentManager.beginTransaction()
                    .remove(fragment).commit()
            }
            dialogView.edtLocation.setOnClickListener {
                alertDialog.dismiss()
            }
        }
        return binding.root
    }

    private fun saveData() {
        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        val phoneNumber = binding.edtPhone.text.toString()
        val location = binding.edtLocation.text.toString()
        changeData(name, email, password, phoneNumber, location)
    }

    private fun changeData(
        name: String,
        email: String,
        password: String,
        phoneNumber: String,
        location: String
    ) {
        saveToDatabase(name, email, password, phoneNumber, location)
        val drawable = binding.imageAvatar.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        lifecycleScope.launch(Dispatchers.IO) {
            imageViewModel.addToData(Image(user.uid, bitmap))
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "Đã cập nhật", Toast.LENGTH_SHORT).show()
                val action = AccountFragmentDirections.actionAccountFragmentToHomeFragment()
                findNavController().navigate(action)
            }

        }
    }

    private fun saveToDatabase(
        name: String,
        email: String,
        password: String,
        phoneNumber: String,
        location: String
    ) {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val dbRef1 = FirebaseStorage.getInstance().getReference("images/$fileName")
        dbRef1.putFile(imageUri).addOnSuccessListener {

        }.addOnFailureListener {
            Toast.makeText(this.requireContext(), "Fail", Toast.LENGTH_LONG).show()
        }
        val userChange = User(
            name,
            email,
            password,
            user.uid,
            fileName.toString(),
            user.token,
            phoneNumber,
            location
        )
        FirebaseDatabase.getInstance().reference.child("User").child(user.uid).setValue(userChange)
        userViewModel.addUser(userChange)
        Log.d("ls", "1")
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == AppCompatActivity.RESULT_OK) {
            imageUri = data?.data!!
            binding.imageAvatar.setImageURI(imageUri)
        }
    }

    private fun getInformationUser(user: User) {
        lifecycleScope.launch(Dispatchers.IO) {
            val image = imageViewModel.getObjectData(user.uid)
            val bitmap = image.bitmap
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            val path =
                MediaStore.Images.Media.insertImage(context?.contentResolver, bitmap, "val", null)
            imageUri = Uri.parse(path)
            withContext(Dispatchers.Main) {
                binding.edtName.setText(user.name)
                binding.edtEmail.setText(user.email)
                binding.edtPassword.setText(user.password)
                binding.edtPhone.setText(user.phoneNumber)
                binding.imageAvatar.setImageBitmap(image.bitmap)
                binding.edtLocation.setText(user.location)
            }

        }
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 123
            )
            return
        }
        val task = locationProviderClient?.lastLocation
        task?.addOnSuccessListener {
            if (it != null) {
                this.currentLocation = it
                val mapFragment = childFragmentManager
                    .findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync(this)
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val latLong = LatLng(currentLocation?.latitude!!, currentLocation?.longitude!!)
        drawMarker(latLong)
        mMap.setOnMapClickListener {
            if (currentMarker != null) {
                currentMarker?.remove()
                val newLatLng = LatLng(it.latitude, it.longitude)
                drawMarker(newLatLng)
            }
        }
    }

    private fun drawMarker(latLong: LatLng) {
        val text = getAddress(latLong.latitude, latLong.longitude)
        val markerOption = MarkerOptions().position(latLong).title("I here")
            .snippet(text)
        Log.d(
            "doanpt",
            "$text"
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLong))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLong, 15f))
        currentMarker = mMap.addMarker(markerOption)
        currentMarker?.showInfoWindow()
        dialogView.edtLocation.text = text
        binding.edtLocation.setText(text)

    }

    private fun getAddress(lat: Double, lon: Double): String? {
        val geoCoder = Geocoder(requireContext(), Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, lon, 1)
        return address[0].getAddressLine(0).toString()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1000 -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            }
        }
    }
}