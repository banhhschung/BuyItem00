package com.example.buyitem00.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.buyitem00.OnClickSupporter
import com.example.buyitem00.databinding.PersonLayoutBinding
import com.example.buyitem00.model.Image
import com.example.buyitem00.model.Supporter
import com.example.buyitem00.model.SupporterWithImage

class PersonAdapter(private val callback: OnClickSupporter) :
    RecyclerView.Adapter<PersonAdapter.ViewHolder>() {

    private var arrSupporter = arrayListOf<SupporterWithImage>()
    fun reloadData(arrSp: ArrayList<SupporterWithImage>) {
        arrSupporter.clear()
        arrSupporter.addAll(arrSp)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: PersonLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(supporter: SupporterWithImage) {
            binding.imgAvatar.setImageBitmap(supporter.avatar)
            binding.tvName.text = supporter.name
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PersonLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrSupporter[position])
        holder.binding.layout.setOnClickListener {
            callback.onclick(arrSupporter[position])
        }
    }

    override fun getItemCount(): Int {
        return arrSupporter.size
    }
}