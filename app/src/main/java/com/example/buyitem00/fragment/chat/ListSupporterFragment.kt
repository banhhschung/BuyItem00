package com.example.buyitem00.fragment.chat

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buyitem00.OnClickSupporter
import com.example.buyitem00.adapter.PersonAdapter
import com.example.buyitem00.data.UserViewModel
import com.example.buyitem00.data.image.ImageViewModel
import com.example.buyitem00.data.supporter.SupporterViewModel
import com.example.buyitem00.databinding.FragmentListSupporterBinding
import com.example.buyitem00.model.Image
import com.example.buyitem00.model.Supporter
import com.example.buyitem00.model.SupporterWithImage
import com.example.buyitem00.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListSupporterFragment : Fragment(), OnClickSupporter {
    private lateinit var binding: FragmentListSupporterBinding
    private val supporterViewModel: SupporterViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val imageViewModel: ImageViewModel by activityViewModels()
    private val adapter = PersonAdapter(this)
    private var arrSupport = arrayListOf<SupporterWithImage>()
    private var userMain = User()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListSupporterBinding.inflate(layoutInflater)

        userViewModel.readAllData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                userMain = User()
            } else {
                userMain = it[0]
            }
        }


        binding.rvSupportMain.layoutManager = LinearLayoutManager(this.requireContext())
        binding.rvSupportMain.adapter = adapter


        supporterViewModel.readAllData.observe(viewLifecycleOwner) {
            for (i in it) {
                arrSupport.clear()
                lifecycleScope.launch(Dispatchers.IO) {
                    val image = imageViewModel.getObjectData(i.uid)
                    arrSupport.add(
                        SupporterWithImage(
                            i.name,
                            i.email,
                            i.password,
                            i.uid,
                            image.bitmap,
                            i.token
                        )
                    )
                    withContext(Dispatchers.Main) {
                        adapter.reloadData(arrSupport)
                    }
                }
            }
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val action =
                    ListSupporterFragmentDirections.actionListSupporterFragmentToMainActivity()
                findNavController().navigate(action)

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(callback)

        return binding.root
    }

    override fun onclick(supporter: SupporterWithImage) {
        val action = ListSupporterFragmentDirections.actionListSupporterFragmentToChatFragment(
            userMain.uid, supporter.uid, supporter.name, supporter.token
        )
        findNavController().navigate(action)
    }
}