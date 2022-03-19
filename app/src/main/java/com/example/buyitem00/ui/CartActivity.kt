package com.example.buyitem00.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.buyitem00.OnclickItemListener
import com.example.buyitem00.adapter.ListCartAdapter
import com.example.buyitem00.cloudmessagingservice.RetrofitInstance
import com.example.buyitem00.data.cart.CartViewModel
import com.example.buyitem00.data.supporter.SupporterViewModel
import com.example.buyitem00.databinding.ActivityCartBinding
import com.example.buyitem00.model.Cart
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
    private var arrCount = arrayListOf<Int>()
    private val adapter = ListCartAdapter(this)
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


        binding.rvCart.layoutManager = GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false)
        binding.rvCart.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO) {
            val arrCart = cartViewModel.findCardByUserId(uid)
            withContext(Dispatchers.Main) {
                for (i in arrCart.indices) {
                    search(arrCart[i])
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