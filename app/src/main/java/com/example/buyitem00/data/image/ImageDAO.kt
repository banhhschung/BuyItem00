package com.example.buyitem00.data.image

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.buyitem00.model.Image

@Dao
interface ImageDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addImageToRoom(image: Image)


    @Query("SELECT * FROM image_table WHERE id =:id")
    suspend fun findObjectImage(id: String): Image

    @Query("DELETE FROM image_table WHERE id =:id")
    fun deleteImage(id: String)

    @Query("SELECT * FROM image_table ORDER BY id ASC")
    fun getAllData(): LiveData<List<Image>>
}