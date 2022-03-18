package com.example.buyitem00.parser

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Converter {
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): ByteArray {
        var outStream = ByteArrayOutputStream()
        if (bitmap.byteCount > 20000000) {
            val reside = Bitmap.createScaledBitmap(
                bitmap, (bitmap.width * 0.8).toInt(),
                (bitmap.height * 0.8).toInt(), true
            )
            reside.compress(Bitmap.CompressFormat.PNG, 100, outStream)
        } else {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream)
        }
        return outStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(byteArray: ByteArray): Bitmap? {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}