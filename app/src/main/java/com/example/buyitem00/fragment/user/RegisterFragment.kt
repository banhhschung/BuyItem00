package com.example.buyitem00.fragment.user

import android.content.Context
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.buyitem00.cloudmessagingservice.Constant.Companion.TOPIC
import com.example.buyitem00.cloudmessagingservice.FirebaseService
import com.example.buyitem00.data.UserViewModel
import com.example.buyitem00.data.image.ImageViewModel
import com.example.buyitem00.databinding.FragmentRegisterBinding
import com.example.buyitem00.model.Image
import com.example.buyitem00.model.User
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

class RegisterFragment : Fragment() {
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
            if (email == "" || password == "" || repeatPassword == "" || name == "") {
                Toast.makeText(
                    this.requireContext(),
                    "Please fill all information of the box",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                register(name, email, password)

            }
        }


        binding.imgAvatar.setOnClickListener {
            selectImage()
        }

        return binding.root
    }

    private fun register(name: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                addToFirebaseDatabase(name, email, password, auth.currentUser!!.uid)
                val drawable = binding.imgAvatar.drawable as BitmapDrawable
                val bitmap = drawable.bitmap

                lifecycleScope.launch(Dispatchers.IO){
                    imageViewMode.addToData(Image(auth.currentUser!!.uid, bitmap))
                    withContext(Dispatchers.Main){
                        val action = RegisterFragmentDirections.actionRegisterFragmentToMainActivity()
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

    private fun addToFirebaseDatabase(name: String, email: String, password: String, uid: String) {

        val formatter = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
        val now = Date()
        val fileName = formatter.format(now)
        val dbRef1 = FirebaseStorage.getInstance().getReference("images/$fileName")
        dbRef1.putFile(imageUri).addOnSuccessListener {
            binding.imgAvatar.setImageURI(null)
        }.addOnFailureListener {
            Toast.makeText(this.requireContext(), "Fail", Toast.LENGTH_LONG).show()

        }

        dbRef = FirebaseDatabase.getInstance().getReference()
        Log.d("doanpt", "$token")
        dbRef.child("User").child(uid)
            .setValue(User(name, email, password, uid, fileName.toString(), token))
        userViewModel.addUser(User(name, email, password, uid, fileName.toString(), token))
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
}