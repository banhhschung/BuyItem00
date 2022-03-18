package com.example.buyitem00.model

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "image_table")
data class Image(
    @PrimaryKey
    val id: String = "",
    val bitmap: Bitmap
)
