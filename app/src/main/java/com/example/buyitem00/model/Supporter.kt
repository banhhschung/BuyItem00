package com.example.buyitem00.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "supporter_table")
data class Supporter(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    @PrimaryKey
    val uid: String = "",
    val avatar: String = "",
    val token: String = ""
)
