package com.example.buyitem00.fragment.user

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.buyitem00.R
import com.example.buyitem00.cloudmessagingservice.Constant.Companion.TOPIC
import com.example.buyitem00.cloudmessagingservice.FirebaseService
import com.example.buyitem00.data.UserViewModel
import com.example.buyitem00.data.image.ImageViewModel
import com.example.buyitem00.databinding.FragmentRegisterBinding
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class RegisterFragment : Fragment(), OnMapReadyCallback {
    private lateinit var dialogView: LayoutDialogBinding
    private lateinit var mMap: GoogleMap
    private var locationProviderClient: FusedLocationProviderClient? = null
    private var currentLocation: Location? = null
    private var currentMarker: Marker? = null
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var userViewModel: UserViewModel
    private val imageViewMode: ImageViewModel by activityViewModels()
    private lateinit var imageUri: Uri
    private var token = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(layoutInflater)
        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        auth = FirebaseAuth.getInstance()
        imageUri = Uri.parse("android.resource://"+ requireContext().packageName +"/drawable/blank_profile")
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            FirebaseService.sharedRref =
                activity?.getSharedPreferences("sharePref", Context.MODE_PRIVATE)
            if (!it.isSuccessful) {
                Log.d("doanpt", it.exception.toString())
                return@addOnCompleteListener
            }
            FirebaseService.token = it.result
            token = it.result
        }
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)

        binding.btnRegister.setOnClickListener {
            val name = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            val repeatPassword = binding.edtRepeatPassword.text.toString()
            val phoneNumber = binding.edtPhoneNumber.text.toString()
            val location = binding.edtLocation.text.toString()
            if (email == "" || password == "" || repeatPassword == "" || name == "" || phoneNumber == "" || location == "") {
                Toast.makeText(
                    this.requireContext(),
                    "Hãy điền đầy đủ thông tin cần thiết",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                register(name, email, password, phoneNumber, location)

            }
        }


        binding.imgAvatar.setOnClickListener {
            selectImage()
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

    private fun register(
        name: String,
        email: String,
        password: String,
        phoneNumber: String,
        location: String
    ) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                addToFirebaseDatabase(
                    name,
                    email,
                    password,
                    auth.currentUser!!.uid,
                    phoneNumber,
                    location
                )
                val drawable = binding.imgAvatar.drawable as BitmapDrawable
                val bitmap = drawable.bitmap
                lifecycleScope.launch(Dispatchers.IO){
                    imageViewMode.addToData(Image(auth.currentUser!!.uid, bitmap))
                    withContext(Dispatchers.Main){
                        val action =
                            RegisterFragmentDirections.actionRegisterFragmentToMainActivity2()
                        findNavController().navigate(action)
                    }
                }





            } else {
                Toast.makeText(this.requireContext(), it.exception?.message, Toast.LENGTH_LONG)
                    .show()
                Log.d("doanpt", it.exception.toString())
            }
        }
    }

    private fun addToFirebaseDatabase(
        name: String,
        email: String,
        password: String,
        uid: String,
        phoneNumber: String,
        location: String
    ) {

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val dbRef1 = FirebaseStorage.getInstance().getReference("images/$fileName")

        dbRef1.putFile(imageUri).addOnSuccessListener {
            binding.imgAvatar.setImageURI(null)
        }.addOnFailureListener {
            Toast.makeText(this.requireContext(), "Fail", Toast.LENGTH_LONG).show()

        }

        dbRef = FirebaseDatabase.getInstance().reference
        Log.d("doanpt", "$token")
        dbRef.child("User").child(uid)
            .setValue(
                User(
                    name,
                    email,
                    password,
                    uid,
                    fileName.toString(),
                    token,
                    phoneNumber,
                    location
                )
            )
        userViewModel.addUser(
            User(
                name,
                email,
                password,
                uid,
                fileName.toString(),
                token,
                phoneNumber,
                location
            )
        )
        Toast.makeText(this.requireContext(), "Successful", Toast.LENGTH_LONG).show()
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
            binding.imgAvatar.setImageURI(imageUri)
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