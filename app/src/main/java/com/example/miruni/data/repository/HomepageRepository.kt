package com.example.miruni.data.repository

import android.util.Log
import com.example.miruni.api.HomepageApi
import com.example.miruni.api.getRetrofit
import com.example.miruni.api.model.HomepageResponse
import retrofit2.Response

class HomepageRepository(private val api: HomepageApi = getRetrofit().create(HomepageApi::class.java)) {
    suspend fun getHomepage(token: String): Response<HomepageResponse>{
        val response = api.getHomepage(token)
        return response
    }

}