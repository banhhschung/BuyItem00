package com.example.buyitem00.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.buyitem00.databinding.ItemCartLayoutBinding
import com.example.buyitem00.model.Order

class OrderListAdapter : RecyclerView.Adapter<OrderListAdapter.ViewHolder>() {
    private val arrOrder = arrayListOf<Order>()

    fun reloadData(arr: ArrayList<Order>) {
        arrOrder.clear()
        arrOrder.addAll(arr)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemCartLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.tvId.text = order.id
            binding.tvLocation.text = order.location
            binding.tvStatus.text = order.status
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCartLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrOrder[position])
    }

    override fun getItemCount(): Int {
        return arrOrder.size
    }
}