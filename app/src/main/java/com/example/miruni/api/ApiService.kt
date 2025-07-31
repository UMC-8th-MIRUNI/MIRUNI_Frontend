package com.example.miruni.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    /**
     * 일정 등록
     */
    @POST("/api/schedule")
    fun registrationSchedule(@Body request: ScheduleToRegister): Call<RegistrationScheduleResponse>


}