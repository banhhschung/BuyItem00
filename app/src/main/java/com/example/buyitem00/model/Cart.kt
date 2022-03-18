package com.example.buyitem00.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "cart_table")
data class Cart(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uiUser: String,
    val idProduct: String,
)
