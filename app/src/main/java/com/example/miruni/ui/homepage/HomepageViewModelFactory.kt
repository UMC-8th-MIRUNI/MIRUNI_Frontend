package com.example.miruni.ui.homepage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.miruni.data.repository.HomepageRepository
import com.example.miruni.ui.storage.StorageViewModel

class HomepageViewModelFactory (private val repository: HomepageRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(StorageViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return HomepageViewModel(repository) as T
        }
        throw IllegalStateException("HomepageViewModel 못 찾음")
    }
}