package com.example.buyitem00.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.buyitem00.data.UserViewModel
import com.example.buyitem00.data.image.ImageViewModel
import com.example.buyitem00.databinding.FragmentSettingBinding
import com.example.buyitem00.model.Image
import com.example.buyitem00.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private val userViewModel: UserViewModel by activityViewModels()
    private val imageViewModel: ImageViewModel by activityViewModels()
    private var user = User()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(layoutInflater)

        userViewModel.readAllData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                user = it[0]
                getInformationUser(user)
            }
        }



        return binding.root
    }

    private fun getInformationUser(user: User) {
        lifecycleScope.launch(Dispatchers.IO) {
           val image = imageViewModel.getObjectData(user.uid)
            withContext(Dispatchers.Main) {
                binding.edtName.setText(user.name)
                binding.edtEmail.setText(user.email)
                binding.edtPassword.setText(user.password)
                binding.edtPhone.setText("")
                binding.imageAvatar.setImageBitmap(image.bitmap)
            }

        }
    }
}