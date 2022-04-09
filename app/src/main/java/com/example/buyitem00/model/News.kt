package com.example.buyitem00.model

import java.io.Serializable

data class News(
    val titleDetail: String = "",
    val nameProduct: String = "",
    val picture: String = "",
    val price: String = "",
    val link: String = ""
) : Serializable
