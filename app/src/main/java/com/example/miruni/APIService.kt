package com.example.miruni

import retrofit2.http.GET
import retrofit2.http.Header

interface APIService {
    @GET("/api/homePage")
    suspend fun getHomepage(@Header("Authorization") token: String) : HomepageResponse
}