package com.example.miruni.ui.memoir

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.miruni.api.model.MemoirCountResponse
import com.example.miruni.api.model.MemoirDateListResponse
import com.example.miruni.api.model.MemoirDeliteResponse
import com.example.miruni.api.model.MemoirDetailResponse
import com.example.miruni.api.model.MemoirSearchResponse
import com.example.miruni.data.repository.MemoirRepository
import kotlinx.coroutines.launch

class MemoirViewModel(private val repository: MemoirRepository): ViewModel() {

    private val CountDate = MutableLiveData<MemoirCountResponse>()
    val countDate : LiveData<MemoirCountResponse> = CountDate
    /* 날짜 별 회고록 갯수 조회 */
    fun memoirCountByDate(token: String){
        viewModelScope.launch {
            try {
                val response = repository.getmemoirCountByDate(token)
                if(response.isSuccessful){
                    CountDate.value = response.body()
                    Log.d("날짜 별 회고록 갯수 조회", "성공: ${response.body()?.result}")

                }else {Log.e("날짜 별 회고록 갯수 조회", "바디 에러: ${response.code()}")}
            }catch (e: Exception){
                Log.e("날짜 별 회고록 갯수 조회", "연결 에러: ${e.message}")
            }
        }
    }


    private val SearchData = MutableLiveData<MemoirSearchResponse>()
    val searchData : LiveData<MemoirSearchResponse> = SearchData
    /* 특정 날짜 회고 목록 조회 */
    fun memoirSearch(token: String, date: String){
        viewModelScope.launch {
            try{
                val response = repository.getMemoirSearch(token, date)
                if (response.isSuccessful) {
                    SearchData.value = response.body()
                    Log.d("특정 날짜 회고 목록 조회", "성공: ${response.body()?.result}")

                }else {Log.e("특정 날짜 회고 목록 조회", "바디 에러: ${response.code()}")}
            }catch (e: Exception) { Log.e("날짜 별 회고록 갯수 조회", "연결에라: ${e.message}")}
        }
    }


    private val ListData = MutableLiveData<MemoirDateListResponse>()
    val listData: LiveData<MemoirDateListResponse> = ListData
    /* 특정 날짜 회고 목록 조회 */
    fun memoirDateList(token: String, date: String){
        viewModelScope.launch {
            try{
                val response = repository.getMemoirDateList(token, date)
                if (response.isSuccessful && response.body()?.result != null) {
                    ListData.value = response.body()
                    Log.d("특정 날짜 회고 목록 조회", "성공: ${response.body()?.result}")

                }else {Log.e("특정 날짜 회고 목록 조회", "바디 에러: ${response.code()}")}
            }catch (e: Exception){ Log.e("특정 날짜 회고 목록 조회", "연결에러: ${e.message}")}
        }
    }


    private val DetailData = MutableLiveData<MemoirDetailResponse>()
    val detailData: LiveData<MemoirDetailResponse> = DetailData
    /* 회고 단일 상세 조회 */
    fun getMemoirDetail(token: String, reviewId: Int){
        viewModelScope.launch {
            try {
                val response = repository.getMemoirDetail(token, reviewId)
                if(response.isSuccessful && response.body()?.result != null){
                    DetailData.value = response.body()
                    Log.d("회고 단일 상세 조회", "성공: ${response.body()?.result}")

                }else {Log.e("회고 단일 상세 조회", "바디 에러: ${response.code()}")}
            }catch (e: Exception) { Log.e("회고 단일 상세 조회", "연결에러:  ${e.message}")}
        }

    }


    private val DeleteData = MutableLiveData<MemoirDeliteResponse>()
    val deleteData: LiveData<MemoirDeliteResponse> = DeleteData
    /* 회고 삭제 */
    fun getMemoirDelete(token: String, reviewId: Int){
        viewModelScope.launch {
            try {
                val response = repository.getMemoirDelete(token, reviewId)
                if(response.isSuccessful && response.body()?.result != null){
                    DeleteData.value = response.body()
                    Log.d("회고 삭제", "성공: ${response.body()?.result}")

                }else {Log.e("회고 삭제", "바디 에러: ${response.code()}")}
            }catch (e: Exception) { Log.e("회고 삭제", "연결에러:  ${e.message}")}
        }
    }
}