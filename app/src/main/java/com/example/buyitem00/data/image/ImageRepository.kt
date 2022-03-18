package com.example.buyitem00.data.image

import androidx.lifecycle.LiveData
import com.example.buyitem00.model.Image

class ImageRepository(private val imageDao: ImageDAO) {

    val readAllData: LiveData<List<Image>> = imageDao.getAllData()

    suspend fun findData(id: String): Image {
        return imageDao.findObjectImage(id)
    }

    suspend fun addToData(image: Image) {
        imageDao.addImageToRoom(image)
    }

    fun deleteImage(id: String) {
        imageDao.deleteImage(id)
    }
}