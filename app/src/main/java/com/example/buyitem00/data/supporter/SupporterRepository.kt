package com.example.buyitem00.data.supporter

import androidx.lifecycle.LiveData
import com.example.buyitem00.model.Supporter

class SupporterRepository(private val supporterDao: SupporterDAO) {
    val readAllData: LiveData<List<Supporter>> = supporterDao.readAllData()
    suspend fun addSupporter(supporter: Supporter) {
        supporterDao.addSupporter(supporter)
    }
}