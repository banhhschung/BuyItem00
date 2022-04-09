package com.example.buyitem00.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.buyitem00.OnclickItemListener
import com.example.buyitem00.adapter.Product1Adapter
import com.example.buyitem00.adapter.Product2Adapter
import com.example.buyitem00.adapter.Product3Adapter
import com.example.buyitem00.cloudmessagingservice.FirebaseService
import com.example.buyitem00.data.UserViewModel
import com.example.buyitem00.data.image.ImageViewModel
import com.example.buyitem00.databinding.FragmentHomeBinding
import com.example.buyitem00.model.Image
import com.example.buyitem00.model.Product
import com.example.buyitem00.model.User
import com.example.buyitem00.ui.ChatActivity
import com.example.buyitem00.ui.DetailActivity
import com.example.buyitem00.ui.ListProductActivity
import com.example.buyitem00.ui.UserActivity
import com.example.buyitem00.vm.ProductViewModel
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment(), OnclickItemListener {
    private lateinit var binding: FragmentHomeBinding
    private val productViewModel: ProductViewModel by viewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private val imageViewModel: ImageViewModel by activityViewModels()
    private lateinit var user: User
    private lateinit var image: Image
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)




        userViewModel.readAllData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                user = User()
            } else {
                user = it[0]
                lifecycleScope.launch(Dispatchers.IO) {
                    image = imageViewModel.getObjectData(user.uid)
                    withContext(Dispatchers.Main) {
                        binding.imageAvatar.setImageBitmap(image.bitmap)
                        binding.tvName.text = "Xin chào, ${user.name}"
                    }
                }
            }
        }


        val phoneAdapter = Product1Adapter(this)
        binding.rvPhones.layoutManager =
            GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
        binding.rvPhones.adapter = phoneAdapter

        productViewModel.arrPhone.observe(this.requireActivity()) {
            phoneAdapter.reloadData(it)
        }
        productViewModel.getArrPhone("Phone")


        val laptopAdapter = Product2Adapter(this)
        binding.rvLaptops.layoutManager =
            GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
        binding.rvLaptops.adapter = laptopAdapter
        productViewModel.arrLaptop.observe(this.requireActivity()) {
            laptopAdapter.reloadData(it)
        }
        productViewModel.getArrLaptop("Laptop")


        val smartwatchAdapter = Product2Adapter(this)
        binding.rvSmartwatches.layoutManager =
            GridLayoutManager(requireContext(), 1, GridLayoutManager.VERTICAL, false)
        binding.rvSmartwatches.adapter = smartwatchAdapter
        productViewModel.arrSmartwatch.observe(this.requireActivity()) {
            smartwatchAdapter.reloadData(it)
        }
        productViewModel.getArrSmartwatch("SmartWatch")


        val tabletAdapter = Product2Adapter(this)
        binding.rvTablet.layoutManager =
            GridLayoutManager(requireContext(), 1, GridLayoutManager.HORIZONTAL, false)
        binding.rvTablet.adapter = tabletAdapter
        productViewModel.arrTablet.observe(this.requireActivity()) {
            tabletAdapter.reloadData(it)
        }
        productViewModel.getArrTablet("Tablet")




        binding.tvPhone.setOnClickListener {
            val intent = Intent(this.context, ListProductActivity::class.java)
            intent.putExtra("list", "phone")
            startActivity(intent)
        }
        binding.tvLaptop.setOnClickListener {
            val intent = Intent(this.context, ListProductActivity::class.java)
            intent.putExtra("list", "laptop")
            startActivity(intent)
        }
        binding.tvSmartwatch.setOnClickListener {
            val intent = Intent(this.context, ListProductActivity::class.java)
            intent.putExtra("list", "smartwatch")
            startActivity(intent)
        }
        binding.tvTablet.setOnClickListener {
            val intent = Intent(this.context, ListProductActivity::class.java)
            intent.putExtra("list", "tablet")
            startActivity(intent)
        }

        binding.btnMessage.setOnClickListener {
            if (user.uid != "") {
                val action = HomeFragmentDirections.actionHomeFragmentToChatActivity()
                findNavController().navigate(action)
            } else {
                openAlertDialog()
            }

        }

        return binding.root
    }

    override fun onClick(product: Product) {
        val intent = Intent(this.requireContext(), DetailActivity::class.java)
        intent.putExtra("product", product)
        startActivity(intent)
    }

    private fun openAlertDialog() {
        val builder = AlertDialog.Builder(this.requireContext())
        builder.setTitle("Waring")
        builder.setMessage("Trước tiên bạn cần phải có một tài khoản")
            .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                val intent = Intent(this.requireContext(), UserActivity::class.java)
                startActivity(intent)
            })
            .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                Toast.makeText(
                    this.requireContext(),
                    "=(((",
                    Toast.LENGTH_LONG
                )
                    .show()
            })
        builder.show()
    }
}