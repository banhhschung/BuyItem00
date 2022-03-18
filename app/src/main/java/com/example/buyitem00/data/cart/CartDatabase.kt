package com.example.buyitem00.data.cart

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.buyitem00.data.UserDatabase
import com.example.buyitem00.model.Cart

@Database(entities = [Cart::class], version = 1, exportSchema = false)
abstract class CartDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDAO


    companion object {
        @Volatile
        private var INSTANCE: CartDatabase? = null


        fun getDatabase(context: Context): CartDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CartDatabase::class.java,
                    "cart_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}