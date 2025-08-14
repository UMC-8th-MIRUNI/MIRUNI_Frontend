package com.example.miruni.ui.storage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.miruni.data.repository.StorageRepository

class StorageViewModelFactory(private val repository: StorageRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(StorageViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return StorageViewModel(repository) as T
        }
        throw IllegalStateException("viewmodel 못 찾음")
    }
}