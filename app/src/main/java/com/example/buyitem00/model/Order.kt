package com.example.buyitem00.model

data class Order(
    val id: String = "",
    val uid: String = "",
    val total: String = "",
    val location: String = "",
    val time: String = "",
    val status: String = "",
    val arrProduct: ArrayList<Product>? = null
)
