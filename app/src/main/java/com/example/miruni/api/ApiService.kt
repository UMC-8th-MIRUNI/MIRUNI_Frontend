package com.example.miruni.api

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    /**
     * 일정 등록
     */
    @POST("/api/schedule")
    fun registrationSchedule(@Body request: ScheduleToRegister): Call<RegistrationScheduleResponse>







    /**  회고 관련  **/

    // 회고 작성 후 저장 API
    @POST("/api/schedule/review")
    suspend fun memoirSave(
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String,
        @Body request: MemoirSaveRequest
    ) : Response<MemoirSaveResponse>

    // 회고 단일 상세 조회 API
    @GET("/api/schedule/review/{reviewId}")
    suspend fun memoirDetail(
        @Header("Authorization") token: String,
        @Path("reviewId") reviewId: Int
    ) : Response<MemoirDetailResponse>

    // 회고 수정 API
    @PATCH("/api/schedule/review/update/{reviewId}")
    suspend fun memoirUpadate(
        @Header("Authorization") token: String,
        @Path("reviewId") reviewId: Int,
        @Body request: MemoirUpdateRequst
    ) : Response<MemoirUpdateResponse>

    // 특정 날짜 회고목록 조회 API
    @GET("/api/schedule/review/date")
    suspend fun memoirDateList(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ) : Response<MemoirDateListResponse>

    // 날짜 별 회고록 갯수 조회 API
    @GET("/api/schedule/review/countByDate")
    suspend fun memoirCountByDate(
        @Header("Authorization") token: String
    ) : Response<MemoirCountResponse>

    // 회고 날짜 검색 조회 API
    @GET("/api/schedule/review/search")
    suspend fun memoirSearch(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ) : Response<MemoirSearchResponse>
}