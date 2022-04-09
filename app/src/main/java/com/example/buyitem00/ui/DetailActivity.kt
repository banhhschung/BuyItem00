package com.example.buyitem00.ui

import android.graphics.drawable.Drawable
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
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
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
import kotlinx.coroutines.invoke
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val cartViewModel: CartViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private lateinit var user: User
    private var cout = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userViewModel.readAllData.observe(this) {
            if (it.isEmpty()) {

            } else {
                user = it[0]
            }
        }
        cartViewModel.readAllData.observe(this) {
            Log.d("doanpt", it.size.toString())
        }

        binding.tvCount.text = "${cout}x"


        binding.tvMinus.setOnClickListener {
            if (cout <= 1) {

            } else {
                cout--
                binding.tvCount.text = "${cout}x"
            }
        }

        binding.tvPlus.setOnClickListener {
            cout++
            binding.tvCount.text = "${cout}x"
        }



        binding.constraint.visibility = View.INVISIBLE
        val product = intent.getSerializableExtra("product") as Product
        Glide.with(binding.imProduct).load(product.image)
            .transform(CenterInside(), RoundedCorners(60))
            .into(binding.imProduct)
        lifecycleScope.launch(Dispatchers.IO) {
            val data = DataMining.getDataItem(product.link)
            withContext(Dispatchers.Main) {
                binding.constraint.visibility = View.VISIBLE
                binding.tvName.text = product.name
                binding.tvAdd.text = "Order for ${product.price}"
                binding.tvInformation.text = data
                val animation = AnimationUtils.loadAnimation(this@DetailActivity, R.anim.translate)
                animation.duration = 200
                binding.constraint.startAnimation(animation)
            }
        }

        binding.btnAddToCart.setOnClickListener {
            if (user.uid != "") {
                cartViewModel.addToCart(
                    Cart(
                        uiUser = user.uid,
                        idProduct = product.id,
                        count = cout
                    )
                )
                Toast.makeText(this, "Success adding", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Mày cần phải tạo tài khoản trước", Toast.LENGTH_LONG).show()
            }
        }
    }
}