package com.example.buyitem00.data.cart

import androidx.lifecycle.LiveData
import com.example.buyitem00.model.Cart
import com.example.buyitem00.model.User

class CartRepository(private val cartDao: CartDAO) {

    val readAllData: LiveData<List<Cart>> = cartDao.readAllData()

    suspend fun addToCart(cart: Cart) {
        cartDao.addToCart(cart)
    }

    fun deleteFromCart(id: String) {
        cartDao.deleteFromCart(id)
    }

    suspend fun findCartByUser(id: String): List<Cart> {
        return cartDao.findCartWithUser(id)
    }
}