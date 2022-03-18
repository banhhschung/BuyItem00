package com.example.buyitem00.data.supporter

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.buyitem00.data.UserDatabase
import com.example.buyitem00.model.Supporter

@Database(entities = [Supporter::class], version = 1, exportSchema = false)
abstract class SupporterDatabase : RoomDatabase() {
    abstract fun supporterDao(): SupporterDAO


    companion object {
        @Volatile
        private var INSTANCE: SupporterDatabase? = null


        fun getDatabase(context: Context): SupporterDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SupporterDatabase::class.java,
                    "supporter_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}