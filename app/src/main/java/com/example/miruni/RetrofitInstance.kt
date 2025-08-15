package com.example.miruni

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {
    private val retrofit by lazy {
        Retrofit.Builder()
//            .baseUrl("http://43.200.133.8:8080/")
            .baseUrl("https://miruni.site/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }
}