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
import com.example.buyitem00.adapter.ListProductAdapter
import com.example.buyitem00.cloudmessagingservice.RetrofitInstance
import com.example.buyitem00.data.UserViewModel
import com.example.buyitem00.data.cart.CartViewModel
import com.example.buyitem00.data.supporter.SupporterViewModel
import com.example.buyitem00.databinding.FragmentCartBinding
import com.example.buyitem00.model.Order
import com.example.buyitem00.model.Product
import com.example.buyitem00.model.Supporter
import com.example.buyitem00.model.User
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

class CardFragment : Fragment(), OnclickItemListener {
    private lateinit var binding: FragmentCartBinding
    private val cartViewModel: CartViewModel by activityViewModels()
    private var arrProduct = arrayListOf<Product>()
    private val adapter = ListProductAdapter(this)
    private val supporterViewModel: SupporterViewModel by activityViewModels()
    private val userViewModel: UserViewModel by activityViewModels()
    private var total = "00.000₫"
    private var supporter = Supporter()
    private var user = User()
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
                            search(arrCart[i].idProduct)
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
                    Order(id, user.uid, total, "", "", "order", arrProduct)
                Firebase.database.getReference("OrderUser").child(id).setValue(order)
                    .addOnSuccessListener {
                        cartViewModel.deleteFromCart(user.uid)
                        Toast.makeText(context, "Done", Toast.LENGTH_LONG).show()
                    }
            }
            PushNotification(
                NotificationData("Thông báo", "Bạn có một đơn đặt hàng mới"),
                supporter.token
            ).apply {
                sendNotification(this)
            }
        }

        return binding.root
    }

    private fun search(idProduct: String) {
        val queryProduct =
            FirebaseDatabase.getInstance().reference.child("Product").orderByChild("id")
                .startAt(idProduct).endAt(idProduct + "\uf8ff")
        queryProduct.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val tempProduct = data.getValue(Product::class.java)!!
                    arrProduct.add(tempProduct)
                    total = ConvertPrice().convert(tempProduct.price, total)
                }
                adapter.reloadData(arrProduct)
                binding.tvPrice.text = total
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