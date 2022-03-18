package com.example.buyitem00.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Slide
import android.transition.Transition
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.viewModels
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.buyitem00.R
import com.example.buyitem00.data.UserViewModel
import com.example.buyitem00.data.cart.CartViewModel
import com.example.buyitem00.databinding.ActivityDetailBinding
import com.example.buyitem00.model.Cart
import com.example.buyitem00.model.Product
import com.example.buyitem00.model.User
import com.example.buyitem00.parser.AutoCreateId
import com.example.buyitem00.parser.DataMining
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val cartViewModel: CartViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var user: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel.readAllData.observe(this) {
            if (it.isEmpty()) {

            } else {
                user = it.get(0)
            }
        }
        cartViewModel.readAllData.observe(this) {
            Log.d("doanpt", it.size.toString())
        }




        binding.constraint.visibility = View.INVISIBLE
        val product = intent.getSerializableExtra("product") as Product
        Glide.with(binding.imProduct).load(product.image).into(binding.imProduct)
        lifecycleScope.launch(Dispatchers.IO) {
            val data = DataMining.getDataItem(product.link)
            withContext(Dispatchers.Main) {
                binding.constraint.visibility = View.VISIBLE
                binding.tvName.text = product.name
                binding.tvPrice.text = product.price
                binding.tvInformation.text = data
                val animation = AnimationUtils.loadAnimation(this@DetailActivity, R.anim.translate)
                animation.duration = 200
                binding.constraint.startAnimation(animation)
            }
        }

        binding.btnAddToCart.setOnClickListener {
            if (user.uid != "") {
                cartViewModel.addToCart(Cart(uiUser = user.uid, idProduct = product.id))
                Toast.makeText(this, "Success adding", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Mày cần phải tạo tài khoản trước", Toast.LENGTH_LONG).show()
            }
        }
    }
}