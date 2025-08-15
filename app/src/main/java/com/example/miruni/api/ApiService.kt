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

    // 월 단위 일정 조회 API
    @GET("/api/schedule")
    suspend fun getScheduleInMonth(
        @Header("Authorization") token: String,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<ScheduleInMonthResponse>

    // 일정 등록 API
    @POST("/api/schedule")
    suspend fun registerSchedule(
        @Header("Authorization") token: String,
        @Body request: RegisterScheduleRequest
    ): Response<RegisterScheduleResponse>

    // AI 일정 쪼개기
    @POST("/api/schedule/{planId}/split")
    suspend fun splitSchedule(
        @Header("Authorization") token: String,
        @Path("planId") scheduleId: Int,
        @Body request: SplitScheduleRequest
    ): Response<SplitScheduleResponse>

    // 일정별 세부 조회 API
    @GET("/api/schedule/{planId}")
    suspend fun getSchedule(
        @Header("Authorization") token: String,
        @Path("planId") taskId: Int
    ): Response<GetSplitScheduleResponse>

    // 일반/AI 일정 수정
    // schedule과 task가 같은 id 체계를 공유함
    @PATCH("/api/schedule/{planId}")
    suspend fun updateSchedule(
        @Header("Authorization") token: String,
        @Path("planId") scheduleId: Int,
        @Body request: UpdateScheduleRequest
    ): Response<ResultOfGetSplitSchedule>

    // 일정 미루기 API
    @PATCH("/api/schedule/{planId}")
    suspend fun delaySchedule(
        @Header("Authorization") token: String,
        @Path("planId") scheduleId: Int,
        @Body request: DelayScheduleRequest
    ): Response<DelayScheduleResponse>

    // 안 한 일정 조회 - 미루지도, 수행하지도 않은 일정 조회
    @GET("/api/schedule/unfinished")
    suspend fun getUnfinishedSchedule(
        @Header("Authorization") token: String
    ): Response<UnfinishedDelayedScheduleResponse>

    // 미룬 일정 조회 - 수행 날짜가 지났지만, 완료되지 않은 일정 조회
    @GET("/api/schedule/delayed")
    suspend fun getDelayedSchedule(
        @Header("Authorization") token: String
    ): Response<UnfinishedDelayedScheduleResponse>

    // 일자별 일정 조회
    @GET("/api/schedule/day")
    suspend fun getScheduleOfDay(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ): Response<GetScheduleOfDayResponse>

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

    // 회고 삭제 API
    @DELETE("/api/schedule/review/{reviewId}")
    suspend fun memoirDelete(
        @Header("AUthorization") token: String,
        @Path("reviewId") review: Int
    ) : Response<MemoirDeliteResponse>
}