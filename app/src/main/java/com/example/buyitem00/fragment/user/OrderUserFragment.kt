package com.example.buyitem00.fragment.user

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.buyitem00.adapter.OrderListAdapter
import com.example.buyitem00.databinding.FragmentOrderUserBinding
import com.example.buyitem00.model.Order
import com.example.buyitem00.model.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderUserFragment : Fragment() {
    private lateinit var binding: FragmentOrderUserBinding
    private val args: OrderUserFragmentArgs by navArgs()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderUserBinding.inflate(layoutInflater)
        val uid = args.uid
        Log.d("doanpt", "$uid")
        val arrOrder = arrayListOf<Order>()

        val adapter = OrderListAdapter()
        binding.rvOrder.layoutManager = GridLayoutManager(context, 1)
        binding.rvOrder.adapter = adapter

        val string = "bWE56uDyeuP4OhJRntfBRWjuImP2"
        FirebaseDatabase.getInstance().getReference().child("OrderUser").orderByChild("uid")
            .startAt(string)
            .endAt(string + "\uf8ff").addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (i in snapshot.children) {
                        val order = i.getValue(Order::class.java)
                        Log.d("doanpt", "$order")
                        if (order != null) {
                            arrOrder.add(order)
                        }
                    }

                    adapter.reloadData(arrOrder)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })

        return binding.root
    }
}