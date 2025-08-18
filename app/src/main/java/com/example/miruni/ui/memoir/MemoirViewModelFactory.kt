package com.example.miruni.ui.memoir

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.miruni.data.repository.MemoirRepository

class MemoirViewModelFactory (private val repository: MemoirRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MemoirViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return MemoirViewModel(repository) as T
        }
        throw IllegalStateException("viewmodel 못 찾음")
    }
}