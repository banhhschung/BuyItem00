package com.example.buyitem00.fragment.user

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.buyitem00.cloudmessagingservice.Constant.Companion.TOPIC
import com.example.buyitem00.cloudmessagingservice.FirebaseService
import com.example.buyitem00.data.UserViewModel
import com.example.buyitem00.data.image.ImageViewModel
import com.example.buyitem00.databinding.FragmentLoginBinding
import com.example.buyitem00.model.Image
import com.example.buyitem00.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private val imageViewModel: ImageViewModel by activityViewModels()
    private lateinit var auth: FirebaseAuth
    private var token = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(layoutInflater)

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


        auth = FirebaseAuth.getInstance()
        binding.edtNew.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment()
            findNavController().navigate(action)
        }


        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()

            if (email == "" || password == "") {
                Toast.makeText(
                    this.requireContext(),
                    "please enter your email and password",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                login(email, password)
            }
        }
        return binding.root
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val gmail = auth.currentUser!!.uid
                getUserFromFirebase(gmail)
            } else {
                Toast.makeText(this.requireContext(), it.exception?.message, Toast.LENGTH_LONG)
                    .show()
                Log.d("doanpt", it.exception?.message.toString())
            }
        }
    }

    private fun getUserFromFirebase(uid: String) {


        FirebaseDatabase.getInstance().reference.child("User")
            .orderByChild("uid").startAt(uid).endAt(uid + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var user = User()
                    for (i in snapshot.children) {
                        user = i.getValue(User::class.java)!!
                    }
                    val temp = User(
                        user.name,
                        user.email,
                        user.password,
                        user.uid,
                        user.avatar,
                        token
                    )
                    userViewModel.addUser(temp)
                    getImage(temp)

                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }


            })
    }

    private fun getImage(user: User) {

        FirebaseDatabase.getInstance().reference.child("User").child(user.uid)
            .setValue(user).addOnCompleteListener {
                Log.d("khanh", "store xong")
//
            }
        val dbRef = FirebaseStorage.getInstance().reference.child("images/${user.avatar}")
        val localFile = File.createTempFile("tempImage", "jpg")
        dbRef.getFile(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            imageViewModel.addToData(Image(user.uid, bitmap))
            Log.d("khanh", "file xong")
            Thread.sleep(1000)
            val action = LoginFragmentDirections.actionLoginFragmentToMainActivity()
            findNavController().navigate(action)
        }

    }
}