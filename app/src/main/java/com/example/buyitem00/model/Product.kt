package com.example.buyitem00.model

import java.io.Serializable

data class Product(
    var id: String = "",
    var type: String = "",
    var name: String = "",
    var price: String = "",
    var image: String = "",
    var link: String = ""
) : Serializable
