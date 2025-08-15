package com.example.miruni.api

import com.example.miruni.api.model.StorageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface StorageApi {
    /* 보관함 페이지 정보 조회*/
    @GET("/api/reports/storage")
    suspend fun getStorage(
        @Header("Authorization") token: String,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<StorageResponse>

    /* 이번달 리포트 오픈 API */
    @POST("/api/reports/{year}/{month}")
    suspend fun openReport(
        @Header("Authorization") token: String
    )
}