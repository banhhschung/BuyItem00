package com.example.buyitem00.data.cart

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.buyitem00.model.Cart

@Dao
interface CartDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToCart(cart: Cart)

    @Query("DELETE FROM cart_table WHERE uiUser =:id")
    fun deleteFromCart(id: String)

    @Query("SELECT * FROM cart_table WHERE uiUser = :idUser")
    suspend fun findCartWithUser(idUser: String): List<Cart>

    @Query("SELECT * FROM cart_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<Cart>>
}