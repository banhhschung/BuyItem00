package com.example.buyitem00.data.image

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.buyitem00.model.Image
import com.example.buyitem00.parser.Converter

@Database(entities = [Image::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class ImageDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDAO

    companion object {
        @Volatile
        private var INSTANCE: ImageDatabase? = null


        fun getDatabase(context: Context): ImageDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ImageDatabase::class.java,
                    "image_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}