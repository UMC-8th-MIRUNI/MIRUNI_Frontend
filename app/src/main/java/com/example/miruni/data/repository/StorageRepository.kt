package com.example.miruni.data.repository

import com.example.miruni.api.StorageApi
import com.example.miruni.api.getRetrofit
import com.example.miruni.api.model.StorageResponse
import retrofit2.Response

class StorageRepository(
    private val api: StorageApi = getRetrofit().create(StorageApi::class.java)
) {
    suspend fun getStorage(token: String, year: Int, month: Int): Response<StorageResponse>? {
        val response = api.getStorage(token, year, month)
        return response

        /* 임시 데이터 반환*/
        /*return StorageResponse(
            peanutCount = 20,
            completionRatePercent = 80,
            isOpenedThisMonth = true,
            canOpenThisMonth = false,
            isOpenedLastMonth = true,
            lockState = "열림",
            isOpenButtonVisible = false
        )*/
        return response
    }
}