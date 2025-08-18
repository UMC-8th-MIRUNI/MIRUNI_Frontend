package com.example.miruni.data.repository

import android.util.Log
import com.example.miruni.api.StorageApi
import com.example.miruni.api.getRetrofit
import com.example.miruni.api.model.MonthOpenResponse
import com.example.miruni.api.model.StorageResponse
import retrofit2.Response

class StorageRepository(
    private val api: StorageApi = getRetrofit().create(StorageApi::class.java)
) {
    suspend fun getStorage(token: String, year: Int, month: Int): Response<StorageResponse>? {
        val response = api.getStorage(token, year, month)
        return response
    }
    suspend fun getOpenReport(token: String, year: Int, month: Int): Response<MonthOpenResponse>?{
        val response = api.openReport(token, year, month)
        if(response.isSuccessful){
            Log.d("이번달 리포트 오픈", "연결 성공: ${response.body()}")
        }else{
            Log.e("이번달 리포트 오픈", "에러 ${response.code()}")
        }
        return response
    }
}