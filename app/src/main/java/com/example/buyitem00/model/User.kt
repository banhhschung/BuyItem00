package com.example.buyitem00.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(

    val name: String = "",
    val email: String = "",
    val password: String = "",
    @PrimaryKey
    val uid: String = "",
    val avatar: String = "",
    val token: String = "",
    val phoneNumber: String = "",
    val location: String = ""
)
