package com.example.buyitem00.data

import androidx.lifecycle.LiveData
import com.example.buyitem00.model.User

class UserRepository(private val userDao: UserDAO) {
    val readAllData: LiveData<List<User>> = userDao.readAllData()
    suspend fun addUser(user: User) {
        userDao.addUser(user)
    }


    suspend fun delete(id: String) {
        userDao.delete(id)
    }

    suspend fun findById(id: String): User {
        return userDao.findUserById(id)
    }
}