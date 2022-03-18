package com.example.buyitem00.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.buyitem00.databinding.MessageReceiveBinding
import com.example.buyitem00.databinding.MessageSendBinding
import com.example.buyitem00.model.Message
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class MessageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val arrMessage = arrayListOf<Message>()
    val ITEM_RECEIVE = 1
    val ITEM_SEND = 2
    fun reloadData(arr: ArrayList<Message>) {
        arrMessage.clear()
        arrMessage.addAll(arr)
        notifyDataSetChanged()
    }

    class SendViewHolder(val binding: MessageSendBinding) : RecyclerView.ViewHolder(binding.root)
    class ReceiveViewHolder(val binding: MessageReceiveBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            val binding = MessageReceiveBinding.inflate(LayoutInflater.from(parent.context))
            return ReceiveViewHolder(binding)
        } else {
            val binding = MessageSendBinding.inflate(LayoutInflater.from(parent.context))
            return SendViewHolder(binding)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = arrMessage[position]
        if (holder.javaClass == SendViewHolder::class.java) {
            val viewHolder = holder as SendViewHolder
            holder.binding.tvSend.text = currentMessage.message
        } else {
            val viewHolder = holder as ReceiveViewHolder
            holder.binding.tvReceive.text = currentMessage.message
        }
    }


    override fun getItemViewType(position: Int): Int {
        val currentMessage = arrMessage[position]
        return if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
            ITEM_SEND
        } else {
            ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return arrMessage.size
    }


}