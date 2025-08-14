package com.example.miruni.api

import com.example.miruni.api.model.HomepageResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface HomepageApi {
    /* 홈페이지 전체 정보 조회 */
    @GET("/api/homePage")
    suspend fun getHomepage(@Header("Authorization") token: String) : Response<HomepageResponse>
}