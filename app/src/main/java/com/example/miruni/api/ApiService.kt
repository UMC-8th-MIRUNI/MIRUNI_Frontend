package com.example.miruni.api


import com.example.miruni.api.model.HomepageResponse
import com.example.miruni.api.model.MemoirCountResponse
import com.example.miruni.api.model.MemoirDateListResponse
import com.example.miruni.api.model.MemoirDeliteResponse
import com.example.miruni.api.model.MemoirDetailResponse
import com.example.miruni.api.model.MemoirSaveRequest
import com.example.miruni.api.model.MemoirSaveResponse
import com.example.miruni.api.model.MemoirSearchResponse
import com.example.miruni.api.model.MemoirUpdateRequst
import com.example.miruni.api.model.MemoirUpdateResponse
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

    // 일정 등록 API
    @POST("/api/schedules")
    suspend fun registerSchedule(
        @Header("Authorization") token: String,
        @Body request: RegisterScheduleRequest
    ): Response<RegisterScheduleResponse>

    // 일정 삭제 API
    @DELETE("/api/schedules")
    suspend fun deleteSchedule(
        @Header("Authorization") token: String,
        @Body request: DeleteScheduleRequest
    ): Response<DeleteScheduleResponse>

    // AI 일정 쪼개기
    @POST("/api/schedules/{planId}")
    suspend fun splitSchedule(
        @Header("Authorization") token: String,
        @Path("planId") scheduleId: Int,
        @Body request: SplitScheduleRequest
    ): Response<SplitScheduleResponse>

    // 일정별 세부 조회 API
    @GET("/api/schedules/{planId}")
    suspend fun getSchedule(
        @Header("Authorization") token: String,
        @Path("planId") scheduleId: Int
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
    @PATCH("/api/schedules/{planId}/timeslot")
    suspend fun delaySchedule(
        @Header("Authorization") token: String,
        @Path("planId") scheduleId: Int,
        @Body request: DelayScheduleRequest
    ): Response<DelayScheduleResponse>

    @PATCH("/api/schedules/{planId}/status/in-progress")
    suspend fun inProgressSchedule(
        @Header("Authorization") token: String,
        @Path("planId") scheduleId: Int,
        @Body request: InProgressScheduleRequest
    ): Response<InProgressScheduleResponse>

    // 일정 상태 완료 변경
    @PATCH("/api/schedules/{planId}/finished")
    suspend fun finishedSchedule(
        @Header("Authorization") token: String,
        @Path("planId") scheduleId: Int,
        @Body request: FinishedScheduleRequest
    ): Response<FinishedScheduleResponse>

    // 안 한 일정 조회 - 미루지도, 수행하지도 않은 일정 조회
    @GET("/api/schedules/unstarted")
    suspend fun getUnstartedSchedule(
        @Header("Authorization") token: String
    ): Response<UnstartedDelayedScheduleResponse>

    // 월 단위 일정 조회 API
    @GET("/api/schedules/monthly")
    suspend fun getScheduleInMonth(
        @Header("Authorization") token: String,
        @Query("year") year: Int,
        @Query("month") month: Int
    ): Response<ScheduleInMonthResponse>

    // 미룬 일정 조회 - 수행 날짜가 지났지만, 완료되지 않은 일정 조회
    @GET("/api/schedules/delayed")
    suspend fun getDelayedSchedule(
        @Header("Authorization") token: String
    ): Response<UnstartedDelayedScheduleResponse>

    // 일자별 일정 조회
    @GET("/api/schedules/daily")
    suspend fun getDailySchedule(
        @Header("Authorization") token: String,
        @Query("date") date: String // yyyy-MM-dd
    ): Response<GetDailyScheduleResponse>


    /**  회고 관련  **/

    // 회고 작성 후 저장 API
    @POST("/api/reviews")
    suspend fun memoirSave(
        @Header("Authorization") token: String,
        @Header("Content-Type") contentType: String,
        @Body request: MemoirSaveRequest
    ) : Response<MemoirSaveResponse>

    // 회고 단일 상세 조회 API
    @GET("/api/reviews/{reviewId}")
    suspend fun memoirDetail(
        @Header("Authorization") token: String,
        @Path("reviewId") reviewId: Int
    ) : Response<MemoirDetailResponse>

    // 회고 수정 API
    @PATCH("/api/reviews/{reviewId}")
    suspend fun memoirUpadate(
        @Header("Authorization") token: String,
        @Path("reviewId") reviewId: Int,
        @Body request: MemoirUpdateRequst
    ) : Response<MemoirUpdateResponse>

    // 특정 날짜 회고목록 조회 API
    @GET("/api/reviews")
    suspend fun memoirDateList(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ) : Response<MemoirDateListResponse>

    // 날짜 별 회고록 갯수 조회 API
    @GET("/api/reviews/days")
    suspend fun memoirCountByDate(
        @Header("Authorization") token: String
    ) : Response<MemoirCountResponse>

    // 회고 날짜 검색 조회 API
    @GET("/api/reviews/days/{date}")
    suspend fun memoirSearch(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ) : Response<MemoirSearchResponse>

    // 회고 삭제 API
    @DELETE("/api/reviews/{reviewId}")
    suspend fun memoirDelete(
        @Header("AUthorization") token: String,
        @Path("reviewId") review: Int
    ) : Response<MemoirDeliteResponse>
}