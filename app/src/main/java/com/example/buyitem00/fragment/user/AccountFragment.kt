package com.example.buyitem00.fragment.user

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.buyitem00.data.UserViewModel
import com.example.buyitem00.data.image.ImageViewModel
import com.example.buyitem00.databinding.FragmentAccountBinding
import com.example.buyitem00.model.Image
import com.example.buyitem00.model.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AccountFragment : Fragment() {
    private lateinit var binding: FragmentAccountBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private val imageViewModel: ImageViewModel by activityViewModels()
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
        return binding.root
    }

    private fun saveData() {
        val name = binding.edtName.text.toString()
        val email = binding.edtEmail.text.toString()
        val password = binding.edtPassword.text.toString()
        changeData(name, email, password)
    }

    private fun changeData(name: String, email: String, password: String) {
        saveToDatabase(name, email, password)
        val drawable = binding.imageAvatar.drawable as BitmapDrawable
        val bitmap = drawable.bitmap
        lifecycleScope.launch(Dispatchers.IO) {
            imageViewModel.addToData(Image(user.uid, bitmap))
            Log.d("ls", "2")
        }
    }

    private fun saveToDatabase(name: String, email: String, password: String) {
        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val dbRef1 = FirebaseStorage.getInstance().getReference("images/$fileName")
        dbRef1.putFile(imageUri).addOnSuccessListener {

        }.addOnFailureListener {
            Toast.makeText(this.requireContext(), "Fail", Toast.LENGTH_LONG).show()
        }
        val userChange = User(name, email, password, user.uid, fileName.toString(), user.token)
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
            withContext(Dispatchers.Main) {
                binding.edtName.setText(user.name)
                binding.edtEmail.setText(user.email)
                binding.edtPassword.setText(user.password)
                binding.edtPhone.setText("000-000-0000")
                binding.imageAvatar.setImageBitmap(image.bitmap)
            }

        }
    }
}