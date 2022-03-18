package com.example.buyitem00.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.buyitem00.model.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class ProductViewModel : ViewModel() {


    private val _arrPhone = MutableLiveData<ArrayList<Product>>()
    val arrPhone: LiveData<ArrayList<Product>>
        get() = _arrPhone

    private val _arrLaptop = MutableLiveData<ArrayList<Product>>()
    val arrLaptop: LiveData<ArrayList<Product>>
        get() = _arrLaptop

    private val _arrSmartwatch = MutableLiveData<ArrayList<Product>>()
    val arrSmartwatch: LiveData<ArrayList<Product>>
        get() = _arrSmartwatch

    private val _arrTablet = MutableLiveData<ArrayList<Product>>()
    val arrTablet: LiveData<ArrayList<Product>>
        get() = _arrTablet

    fun getArrPhone(str: String) {
        val arr = arrayListOf<Product>()
        val database =
            FirebaseDatabase.getInstance().getReference().child("Product").orderByChild("type")
                .startAt(str).endAt(str + "\uf8ff")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val product = data.getValue(Product::class.java)
                    if (product != null) {
                        arr.add(product)
                    }
                }
                _arrPhone.postValue(arr)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getArrLaptop(str: String) {
        val arr = arrayListOf<Product>()
        val database =
            FirebaseDatabase.getInstance().getReference().child("Product").orderByChild("type")
                .startAt(str).endAt(str + "\uf8ff")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val product = data.getValue(Product::class.java)
                    if (product != null) {
                        arr.add(product)
                    }
                }
                _arrLaptop.postValue(arr)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getArrSmartwatch(str: String) {
        val arr = arrayListOf<Product>()
        val database =
            FirebaseDatabase.getInstance().getReference().child("Product").orderByChild("type")
                .startAt(str).endAt(str + "\uf8ff")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val product = data.getValue(Product::class.java)
                    if (product != null) {
                        arr.add(product)
                    }
                }
                _arrSmartwatch.postValue(arr)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    fun getArrTablet(str: String) {
        val arr = arrayListOf<Product>()
        val database =
            FirebaseDatabase.getInstance().getReference().child("Product").orderByChild("type")
                .startAt(str).endAt(str + "\uf8ff")
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val product = data.getValue(Product::class.java)
                    if (product != null) {
                        arr.add(product)
                    }
                }
                _arrTablet.postValue(arr)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}