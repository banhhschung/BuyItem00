package com.example.buyitem00.data.supporter

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.buyitem00.model.Supporter

@Dao
interface SupporterDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addSupporter(supporter: Supporter)


    @Query("SELECT * FROM supporter_table ORDER BY uid ASC")
    fun readAllData() : LiveData<List<Supporter>>


}