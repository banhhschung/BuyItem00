package com.example.buyitem00.parser

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import java.io.File

object GetImage {
    fun fromFirebaseStore(imageLink: String): Bitmap? {
        var bitmap: Bitmap? = null
        val dbRef = FirebaseStorage.getInstance().getReference().child("images/$imageLink")
        val localFile = File.createTempFile("tempImage", "jpg")
        dbRef.getFile(localFile).addOnSuccessListener {
            bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
        }.addOnFailureListener {
            Log.d("doanpt", "error in get image firebase storage")
        }
        return bitmap
    }
}