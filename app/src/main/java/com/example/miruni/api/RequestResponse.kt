package com.example.miruni.api

import com.google.gson.annotations.SerializedName

/** Request Data Class */

/**
 * 일정 등록 요청 시 데이터 클래스
 * POST api/schedule
 */
data class ScheduleToRegister(
    @SerializedName(value = "title") var title: String,
    @SerializedName(value = "description") var description: String,
    @SerializedName(value = "deadline") var deadline: String // "2025-07-30T10:06:42.112Z" yyyy-mm-dd'T'hh:mm:ss.sss
)

/** Response Data Class */

/**
 * 일정 등록 요청 시 응답 데이터 클래스
 * POST api/schedule
 */
data class RegistrationScheduleResponse(
    var planId: Int,
    var title: String,
    var deadline: String, // "2025-07-30T10:06:42.113Z"
    var isDone: Boolean
)