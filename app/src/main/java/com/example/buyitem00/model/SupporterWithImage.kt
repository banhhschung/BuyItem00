package com.example.buyitem00.model

import android.graphics.Bitmap


data class SupporterWithImage(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val uid: String = "",
    val avatar: Bitmap,
    val token: String = ""
)
