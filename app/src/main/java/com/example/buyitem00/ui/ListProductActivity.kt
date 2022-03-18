package com.example.buyitem00.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buyitem00.OnclickItemListener
import com.example.buyitem00.adapter.ListProductAdapter
import com.example.buyitem00.databinding.ActivityListProductBinding
import com.example.buyitem00.model.Product
import com.example.buyitem00.vm.ProductViewModel

class ListProductActivity : AppCompatActivity(), OnclickItemListener {
    private lateinit var binding: ActivityListProductBinding
    private val viewModel: ProductViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListProductBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val adapter = ListProductAdapter(this)
        binding.rvProduct.layoutManager = LinearLayoutManager(this)
        binding.rvProduct.adapter = adapter


        when (intent.getSerializableExtra("list") as String) {
            "phone" -> {
                viewModel.arrPhone.observe(this) {
                    adapter.reloadData(it)
                }
                viewModel.getArrPhone("Phone")
            }
            "laptop" -> {
                viewModel.arrLaptop.observe(this) {
                    adapter.reloadData(it)
                }
                viewModel.getArrLaptop("Laptop")
            }
            "smartwatch" -> {
                viewModel.arrSmartwatch.observe(this) {
                    adapter.reloadData(it)
                }
                viewModel.getArrSmartwatch("SmartWatch")
            }
            "tablet" -> {
                viewModel.arrTablet.observe(this) {
                    adapter.reloadData(it)
                }
                viewModel.getArrTablet("Tablet")
            }
        }


    }

    override fun onClick(product: Product) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("product", product)
        startActivity(intent)
    }
}