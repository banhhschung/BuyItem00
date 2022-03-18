package com.example.buyitem00.data.image

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.buyitem00.model.Image
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageViewModel(application: Application) : AndroidViewModel(application) {

    val getAllData: LiveData<List<Image>>
    private val repository: ImageRepository


    init {
        val imageDao = ImageDatabase.getDatabase(application).imageDao()
        repository = ImageRepository(imageDao)
        getAllData = repository.readAllData
    }


    fun addToData(image: Image) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addToData(image)
        }
    }

    suspend fun getObjectData(id: String): Image {
        val image = viewModelScope.async(Dispatchers.IO) {
            repository.findData(id)
        }
        return image.await()
    }

    fun deleteImage(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteImage(id)
        }
    }
}