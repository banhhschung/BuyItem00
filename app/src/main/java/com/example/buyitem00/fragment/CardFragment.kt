package com.example.buyitem00.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.buyitem00.OnclickItemListener
import com.example.buyitem00.adapter.ListCartAdapter
import com.example.buyitem00.cloudmessagingservice.RetrofitInstance
import com.example.buyitem00.data.UserViewModel
import com.example.buyitem00.data.cart.CartViewModel
import com.example.buyitem00.data.supporter.SupporterViewModel
import com.example.buyitem00.databinding.FragmentCartBinding
import com.example.buyitem00.model.*
import com.example.buyitem00.model.notification.NotificationData
import com.example.buyitem00.model.notification.PushNotification
import com.example.buyitem00.parser.AutoCreateId
import com.example.buyitem00.parser.ConvertPrice
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.math.BigInteger

class CardFragment : Fragment(), OnclickItemListener {
    private lateinit var binding: FragmentCartBinding
    private val cartViewModel: CartViewModel by activityViewModels()
    private var arrProduct = arrayListOf<Product>()
    private var arrCount = arrayListOf<Int>()
    private val adapter = ListCartAdapter(this)
    private val supporterViewModel: SupporterViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private var total = "00.000???"
    private var supporter = Supporter()
    private var user = User()
    private val deliveryCost = "100.000???"
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(layoutInflater)
        userViewModel.readAllData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                user = it[0]
                lifecycleScope.launch(Dispatchers.IO) {
                    val arrCart = cartViewModel.findCardByUserId(user.uid)
                    withContext(Dispatchers.Main) {
                        for (i in arrCart.indices) {
                            search(arrCart[i])
                        }
                    }
                }
            }
        }
        supporterViewModel.readAllData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                supporter = it[0]
            }
        }

        binding.rvCart.layoutManager = GridLayoutManager(context, 1)
        binding.rvCart.adapter = adapter



        binding.btnGet.setOnClickListener {
            if (arrProduct.isNotEmpty()) {
                val id = AutoCreateId().randomId()
                val order =
                    Order(id, user.uid, total, user.location, "", "?????t h??ng", arrProduct)
                Firebase.database.getReference("OrderUser").child(id).setValue(order)
                    .addOnSuccessListener {
                        cartViewModel.deleteFromCart(user.uid)
                        Toast.makeText(context, "Done", Toast.LENGTH_LONG).show()
                    }
            }
            PushNotification(
                NotificationData("Th??ng b??o", "B???n c?? m???t ????n ?????t h??ng m???i"),
                supporter.token
            ).apply {
                sendNotification(this)
            }
        }

        return binding.root
    }

    private fun search(cart: Cart) {
        val queryProduct =
            FirebaseDatabase.getInstance().reference.child("Product").orderByChild("id")
                .startAt(cart.idProduct).endAt(cart.idProduct + "\uf8ff")
        queryProduct.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val tempProduct = data.getValue(Product::class.java)!!
                    arrCount.add(cart.count)
                    arrProduct.add(tempProduct)
                    total = ConvertPrice().convert(tempProduct.price, total, cart.count)
                }
                adapter.reloadData(arrProduct, arrCount)
                binding.tvPrice.text = total
                binding.tvDelivery.text = deliveryCost
                binding.tvTotal.text = ConvertPrice().convert(deliveryCost, total, 1)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun sendNotification(notification: PushNotification) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.postNotification(notification)
                if (response.isSuccessful) {
//                    Log.d("doanpt", "${Gson().toJson(response)}")
                } else {
                    Log.d("doanpt", response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.d("doanpt", "${e.toString()}")
            }
        }

    override fun onClick(product: Product) {
        TODO("Not yet implemented")
    }
}