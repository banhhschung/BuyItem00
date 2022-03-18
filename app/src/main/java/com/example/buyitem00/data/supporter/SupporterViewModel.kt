package com.example.buyitem00.data.supporter

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.buyitem00.model.Supporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SupporterViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<Supporter>>
    private val repository: SupporterRepository


    init {
        val supporterDao = SupporterDatabase.getDatabase(application).supporterDao()
        repository = SupporterRepository(supporterDao)
        readAllData = repository.readAllData
    }


    fun addSupporter(supporter: Supporter){
        viewModelScope.launch(Dispatchers.IO){
            repository.addSupporter(supporter)
        }
    }

}