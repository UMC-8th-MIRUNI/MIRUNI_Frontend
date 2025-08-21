package com.example.miruni.api

import com.example.miruni.api.model.DeleteTaskRequest
import com.example.miruni.api.model.DeleteTaskResponse
import com.example.miruni.api.model.HomepageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface HomepageApi {
    /* 홈페이지 전체 정보 조회 */
    @GET("/api/users/home")
    suspend fun getHomepage(
        @Header("Authorization") token: String
    ) : Response<HomepageResponse>

    /* 일정 삭제 */
    @DELETE("/api/schedules")
    suspend fun deleteTask(
        @Header("Authorization") token: String,
        @Body request: DeleteTaskRequest
    ): Response<DeleteTaskResponse>

    // 일정별 세부 조회 API
    @GET("/api/schedules/{planId}")
    suspend fun getSchedule(
        @Header("Authorization") token: String,
        @Path("planId") planId: Int
    ): Response<GetSplitScheduleResponse>
}