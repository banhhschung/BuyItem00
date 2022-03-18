package com.example.buyitem00.data.cart

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.buyitem00.model.Cart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<Cart>>
    private val repository: CartRepository


    init {
        val cartDao = CartDatabase.getDatabase(application).cartDao()
        repository = CartRepository(cartDao)
        readAllData = repository.readAllData
    }


    fun addToCart(cart: Cart) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addToCart(cart)
        }
    }


    fun deleteFromCart(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteFromCart(id)
        }
    }

    suspend fun findCardByUserId(id: String): List<Cart> {
        val arrCart = viewModelScope.async(Dispatchers.IO) {
            repository.findCartByUser(id)
        }
        return arrCart.await()
    }
}