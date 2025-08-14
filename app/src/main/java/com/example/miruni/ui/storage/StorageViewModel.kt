package com.example.miruni.ui.storage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miruni.api.model.StorageResponse
import com.example.miruni.data.repository.StorageRepository
import kotlinx.coroutines.launch

class StorageViewModel(private val repository: StorageRepository) : ViewModel() {

    /* 보관함 전체 정보 */
    private val StorageData = MutableLiveData<StorageResponse>()
    val storageData: LiveData<StorageResponse> = StorageData
    /* 보관함 퍼센티지 */
    val Percent = MutableLiveData(0)
    val percent: LiveData<Int> = Percent

    fun loadStorage(token: String, year: Int, month: Int){
        viewModelScope.launch {
            try {
                val storage = repository.getStorage(token, year, month)
                StorageData.value = storage?.body()!!
                if(storage.isSuccessful){
                    Log.d("보관함 페이지 조회", "보관함 정보: ${storage.body()}")

                    loadPercent(storage.body()?.completionRatePercent ?: 0)
                }else{
                    Log.e("보관함 페이지 조회", "에러: ${storage.code()} - ${storage.message()}")
                }
            }catch (e: Exception){
                Log.e("보관함 페이지 조회", "실패: ${e.message}")
            }
        }
    }

    fun loadPercent(num: Int){
        Percent.value = num
    }


}