package com.example.miruni.data.repository

import com.example.miruni.api.GetSplitScheduleResponse
import com.example.miruni.api.HomepageApi
import com.example.miruni.api.getRetrofit
import com.example.miruni.api.model.DeleteTaskRequest
import com.example.miruni.api.model.DeleteTaskResponse
import com.example.miruni.api.model.HomepageResponse
import retrofit2.Response

class HomepageRepository(private val api: HomepageApi = getRetrofit().create(HomepageApi::class.java)) {
    suspend fun getHomepage(token: String): Response<HomepageResponse> {
        val response = api.getHomepage(token)
        return response
    }
    suspend fun deleteTask(token: String, deleteTaskRequest: DeleteTaskRequest): Response<DeleteTaskResponse>{
        val response = api.deleteTask(token, deleteTaskRequest)
        return response
    }
    suspend fun getSchedule(token: String, planId: Int): Response<GetSplitScheduleResponse>{
        val response = api.getSchedule(token, planId)
        return response
    }

}