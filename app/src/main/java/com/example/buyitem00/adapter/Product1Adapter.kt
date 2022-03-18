package com.example.buyitem00.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.buyitem00.OnclickItemListener
import com.example.buyitem00.databinding.Product1LayoutBinding
import com.example.buyitem00.model.Product
import java.util.ArrayList

class Product1Adapter(private val callback: OnclickItemListener) :
    RecyclerView.Adapter<Product1Adapter.ViewHolder>() {
    private var arrProduct = arrayListOf<Product>()
    fun reloadData(arr: ArrayList<Product>) {
        arrProduct.clear()
        for (i in 0 until arr.size) {
            if (i <= 5) {
                arrProduct.add(arr[i])
            }
        }
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: Product1LayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvName.text = product.name
            binding.tvPrice.text = product.price
            Glide.with(binding.imProduct).load(product.image).into(binding.imProduct)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = Product1LayoutBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrProduct[position])
        holder.binding.imProduct.setOnClickListener {
            callback.onClick(arrProduct[position])
        }
    }

    override fun getItemCount(): Int {
        return arrProduct.size
    }
}