package com.example.buyitem00.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.buyitem00.model.User

@Dao
interface UserDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUser(user: User)

    @Query("DELETE FROM user_table WHERE uid = :id")
    fun delete(id: String)

    @Query("SELECT * FROM user_table ORDER BY uid ASC")
    fun readAllData(): LiveData<List<User>>

    @Query("SELECT * FROM user_table WHERE uid = :id")
    suspend fun findUserById(id: String): User


}