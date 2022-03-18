package com.example.buyitem00.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
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
import com.example.buyitem00.data.UserViewModel
import com.example.buyitem00.data.cart.CartViewModel
import com.example.buyitem00.data.image.ImageViewModel
import com.example.buyitem00.databinding.FragmentUserBinding
import com.example.buyitem00.model.Image
import com.example.buyitem00.model.User
import com.example.buyitem00.parser.GetImage
import com.example.buyitem00.ui.UserActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class UserFragment : Fragment() {
    private lateinit var binding: FragmentUserBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private val imageViewModel: ImageViewModel by activityViewModels()
    private val cartViewModel: CartViewModel by activityViewModels()
    private lateinit var user: User
    private lateinit var image: Image
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserBinding.inflate(layoutInflater)


        userViewModel.readAllData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                user = User()
            } else {
                user = it[0]
                getInformationUser(user)
            }
        }

        binding.yourAccount.setOnClickListener {

        }

        binding.yourCart.setOnClickListener {
            val action = UserFragmentDirections.actionUserFragmentToCartActivity(user.uid)
            findNavController().navigate(action)
        }

        binding.yourOrder.setOnClickListener {
            val action = UserFragmentDirections.actionUserFragmentToOrderUserFragment(user.uid)
            findNavController().navigate(action)
        }


        binding.logout.setOnClickListener {
            openAlertDialog()
        }


        return binding.root
    }

    private fun getInformationUser(user: User) {
        lifecycleScope.launch(Dispatchers.IO) {
            image = imageViewModel.getObjectData(user.uid)
            withContext(Dispatchers.Main) {
                binding.tvName.text = user.name
                binding.imgAvatar.setImageBitmap(image.bitmap)
            }

        }
    }

    private fun openAlertDialog() {
        val builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle("Waring")
        builder.setMessage("To logout, your cart and your information will be delete from this mobile")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                userViewModel.deleteUser(user.uid)
                imageViewModel.deleteImage(user.uid)
                cartViewModel.deleteFromCart(user.uid)
                Firebase.messaging.deleteToken()
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                Toast.makeText(
                    this.requireContext(),
                    "Không đồng ý thì chết m* mày đi",
                    Toast.LENGTH_LONG
                )
                    .show()
            })
        builder.show()
    }
}