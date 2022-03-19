package com.example.buyitem00.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.buyitem00.OnclickItemListener
import com.example.buyitem00.databinding.RecycleviewProductForCartBinding

import com.example.buyitem00.model.Product
import java.util.ArrayList

class ListCartAdapter(private val callback: OnclickItemListener) :
    RecyclerView.Adapter<ListCartAdapter.ViewHolder>() {
    private var arrProduct = arrayListOf<Product>()
    private var arrCount = arrayListOf<Int>()
    fun reloadData(arrP: ArrayList<Product>, arrC: ArrayList<Int>) {
        arrProduct.clear()
        arrCount.clear()


        arrProduct.addAll(arrP)
        arrCount.addAll(arrC)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: RecycleviewProductForCartBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product, count: Int) {
            Glide.with(binding.imProduct).load(product.image).into(binding.imProduct)
            binding.tvName.text = product.name
            binding.tvPrice.text = product.price
            binding.tvCount.text = "${count}x"
        }
    }

    override fun getItemCount(): Int {
        return arrProduct.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecycleviewProductForCartBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrProduct[position], arrCount[position])
        holder.binding.constraintMain.setOnClickListener {
            callback.onClick(arrProduct[position])
        }
    }
}