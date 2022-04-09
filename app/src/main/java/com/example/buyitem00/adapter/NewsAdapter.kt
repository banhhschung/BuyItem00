package com.example.buyitem00.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.buyitem00.OnClickNews
import com.example.buyitem00.databinding.ItemNewsLayoutBinding
import com.example.buyitem00.model.News
import java.util.ArrayList

class NewsAdapter(private val callback: OnClickNews) :
    RecyclerView.Adapter<NewsAdapter.ViewHolder>() {
    private val arrNews = arrayListOf<News>()
    fun reloadData(arr: ArrayList<News>) {
        arrNews.clear()
        arrNews.addAll(arr)
        notifyDataSetChanged()
    }

    class ViewHolder(val binding: ItemNewsLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(new: News) {
            binding.tvNews.text = new.titleDetail
            Glide.with(binding.imgNews).load(new.picture).into(binding.imgNews)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNewsLayoutBinding.inflate(LayoutInflater.from(parent.context))
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(arrNews[position])
        holder.binding.imgNews.setOnClickListener {
            callback.onclick(arrNews[position])
        }
    }

    override fun getItemCount(): Int {
        return arrNews.size
    }
}