package com.example.buyitem00.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buyitem00.OnclickItemListener
import com.example.buyitem00.adapter.ListProductAdapter
import com.example.buyitem00.cloudmessagingservice.RetrofitInstance
import com.example.buyitem00.data.cart.CartViewModel
import com.example.buyitem00.data.supporter.SupporterViewModel
import com.example.buyitem00.databinding.ActivityCartBinding
import com.example.buyitem00.model.Order
import com.example.buyitem00.model.Product
import com.example.buyitem00.model.Supporter
import com.example.buyitem00.model.notification.NotificationData
import com.example.buyitem00.model.notification.PushNotification
import com.example.buyitem00.parser.AutoCreateId
import com.example.buyitem00.parser.ConvertPrice
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception

class CartActivity : AppCompatActivity(), OnclickItemListener {

    private lateinit var binding: ActivityCartBinding
    private val cartViewModel: CartViewModel by viewModels()
    private var arrProduct = arrayListOf<Product>()
    private val adapter = ListProductAdapter(this)
    private val supporterViewModel: SupporterViewModel by viewModels()
    private var total = "00.000₫"
    private var supporter = Supporter()
    private val arg: CartActivityArgs by navArgs()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uid = arg.uid

        supporterViewModel.readAllData.observe(this) {
            if (it.isNotEmpty()) {
                supporter = it[0]
            }
        }


        binding.rvCart.layoutManager = LinearLayoutManager(this)
        binding.rvCart.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO) {
            val arrCart = cartViewModel.findCardByUserId(uid)
            withContext(Dispatchers.Main) {
                for (i in arrCart.indices) {
                    search(arrCart[i].idProduct, arrCart[i].count)
                }
            }
        }

        binding.btnGet.setOnClickListener {
            if (arrProduct.isNotEmpty()) {
                val id = AutoCreateId().randomId()
                val order =
                    Order(id, uid, total, "", "", "order", arrProduct)
                Firebase.database.getReference("OrderUser").child(id).setValue(order)
                    .addOnSuccessListener {
                        cartViewModel.deleteFromCart(uid)
                        Toast.makeText(applicationContext, "Done", Toast.LENGTH_LONG).show()
                    }
            }
            PushNotification(
                NotificationData("Thông báo", "Bạn có một đơn đặt hàng mới"),
                supporter.token
            ).apply {
                sendNotification(this)
            }
        }

    }

    private fun search(id: String, count: Int) {
        val queryProduct =
            FirebaseDatabase.getInstance().reference.child("Product").orderByChild("id")
                .startAt(id).endAt(id + "\uf8ff")
        queryProduct.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val tempProduct = data.getValue(Product::class.java)!!
                    arrProduct.add(tempProduct)
                    total = ConvertPrice().convert(tempProduct.price, total, count)
                }
                adapter.reloadData(arrProduct)
                binding.tvPrice.text = total
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun onClick(product: Product) {
        TODO("Not yet implemented")
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

}