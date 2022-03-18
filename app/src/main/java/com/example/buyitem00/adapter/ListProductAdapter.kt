package com.example.buyitem00.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.buyitem00.OnclickItemListener
import com.example.buyitem00.databinding.RecycleviewProductBinding
import com.example.buyitem00.model.Product
import java.util.ArrayList

class ListProductAdapter(private val callback: OnclickItemListener) :
    RecyclerView.Adapter<ListProductAdapter.ViewHolder>() {
    private var arrProduct = arrayListOf<Product>()
    fun reloadData(arr: ArrayList<Product>) {
        arrProduct.clear()
        arrProduct.addAll(arr)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: RecycleviewProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            Glide.with(binding.imProduct).load(product.image).into(binding.imProduct)
            binding.tvName.text = product.name
            binding.tvPrice.text = product.price
        }
    }

    override fun getItemCount(): Int {
        return arrProduct.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecycleviewProductBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrProduct[position])
        holder.binding.constraintMain.setOnClickListener {
            callback.onClick(arrProduct[position])
        }
    }
}