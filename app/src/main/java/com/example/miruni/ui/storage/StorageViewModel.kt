package com.example.miruni.ui.storage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miruni.api.model.MonthOpenResponse
import com.example.miruni.api.model.StorageResponse
import com.example.miruni.data.repository.StorageRepository
import kotlinx.coroutines.launch

class StorageViewModel(private val repository: StorageRepository) : ViewModel() {

    private val StorageData = MutableLiveData<StorageResponse>()
    val storageData: LiveData<StorageResponse> = StorageData
    /* 보관함 전체 정보 */
    fun loadStorage(token: String, year: Int, month: Int){
        viewModelScope.launch {
            try {
                val storage = repository.getStorage(token, year, month)

                if(storage != null && storage.isSuccessful){
                    Log.d("보관함 페이지 조회", "보관함 정보: ${storage.body()}")
                    Log.d("보관함 페이지 조회", "보관함 정보: ${storage.raw()}")
                    StorageData.value = storage?.body()
                    loadPercent(storage.body()?.result?.completionRatePercent ?: 0)
                }else{
                    Log.e("보관함 페이지 조회", "에러: ${storage?.code()} - ${storage?.message()}")
                }
            }catch (e: Exception){
                Log.e("보관함 페이지 조회", "실패: ${e}")
            }
        }
    }


    val Percent = MutableLiveData(0)
    val percent: LiveData<Int> = Percent
    /* 보관함 퍼센티지 */
    fun loadPercent(num: Int){
        Percent.value = num
    }

    private val CheckReport = MutableLiveData<MonthOpenResponse>()
    val checkReport: LiveData<MonthOpenResponse> = CheckReport
    /* 이번달 리포트 오픈 */
    fun loadMonthReport(token: String, year: Int, month: Int){
        viewModelScope.launch {
            try {
                val response = repository.getOpenReport(token, year, month)
                if(response!= null && response.isSuccessful){
                    Log.d("이번달 리포트 오픈", "성공: ${response.body()}")
                    CheckReport.value = response.body()
                }
            }catch (e: Exception){
                Log.e("이번달 리포트 오픈", "에러: ${e.message}")
            }
        }
    }

}