package com.example.miruni.api

/** Request Data Class */
/**
 * 일정 등록 요청 시 데이터 클래스
 * POST api/schedule
 */
data class RegisterScheduleRequest(
    val title: String, // 제목
    val deadline: String, // 마감 일시
    val scheduledStart: String, // "2025-08-14T16:19:13.711Z"
    val scheduledEnd: String,
    val priority: String,
    val description: String
)

/**
 * 일정 삭제
 */
data class DeleteScheduleRequest(
    val category: String,
    val planId: Int,
    val aiPlanIds: List<Int>
)

/**
 * AI 일정 쪼개기 API
 * POST /api/schedule/{planId}
 */
data class SplitScheduleRequest(
    val planType: String,
    val taskRange: String,
    val detailRequest: String
)

/**
 * 일반/AI 일정 수정 - plans는 SplitSchedule 사용
 */
data class UpdateScheduleRequest(
    val category: String, // BASIC | AI
    val title: String,
    val deadline: String, // yyyy-MM-ddThh:mm:ss
    val priority: String, // HIGH | MEDIUM | LOW
    val scheduledStart: String, // yyyy-MM-ddThh:mm:ss
    val scheduledEnd: String, // yyyy-MM-ddThh:mm:ss
    val description: String,
    val delete: Boolean
)

/**
 * 일정 미루기
 */
data class DelayScheduleRequest(
    val newStartDateTime: String, // 새롭게 설정한 datetime "yyyy-MM-ddThh:mm:ss.___"
    val category: String, // AI가 쪼깬 건지, 안 쪼갠 건지
    val executeTime: Int, // 실제 소요된 시간
    val actualStart: Int // 실제 시작한 시각
)

/**
 * inProgressSchedule
 */
data class InProgressScheduleRequest(
    val category: String
)

/**
 * 일정 상태 완료 변경
 */
data class FinishedScheduleRequest(
    val category: String,
    val executeTime: Int,
    val actualStart: Int
)


/** Response Data Class
 * =====================================================
 */

/**
 * 일정 등록
 * POST api/schedule
 */
data class RegisterScheduleResponse(
    val errorCode: String?,
    val message: String,
    val result: ResultOfRegisterSchedule
)
data class ResultOfRegisterSchedule(
    val planId: Int,
    val title: String,
    val deadline: String, // "2025-08-14T16:19:13.711Z"
    val scheduledStart: String, // "2025-08-14T16:19:13.711Z"
    val isDone: Boolean
)

/**
 * 일정 삭제
 */
data class DeleteScheduleResponse(
    val requested: Int,
    val deleted: Int,
    val notFound: List<Int>,
    val unauthorized: List<Int>
)

/**
 * AI 일정 쪼개기 응답
 * POST /api/schedule/{planId}/split
 */
data class SplitScheduleResponse(
    val errorCode: String?,
    val message: String,
    val result: List<PostSplitSchedule>
)
data class PostSplitSchedule(
    val id: Int,
    val description: String,
    val expectedDuration: Int,
    val scheduledStart: String, // "yyyy-MM-ddThh:mm:ss"
    val scheduledEnd: String
)

/**
 * 일정별 세부 조회
 * GET /api/schedule/{planId}
 * planId = taskId
 */
data class GetSplitScheduleResponse(
    val errorCode: String?,
    val message: String,
    val result: ResultOfGetSplitSchedule
)
data class ResultOfGetSplitSchedule(
    val category: String, // BASIC | AI
    val title: String, // 대주제
    val deadline: String,
    val taskRange: String,
    val priority: String,
    val plans: List<SplitSchedule>
)
data class SplitSchedule( // 소주제
    val planId: Int,
    val date: String, // yyyy-MM-dd
    val description: String,
    val expectedDuration: Int,
    val startTime: String, // hh:mm:ss
    val endTime: String, // hh:mm:ss
)

/**
 * 일반/AI 일정 수정
 */
data class UpdateScheduleResponse(
    val planId: Int,
    val updatedAt: String // yyyy-MM-ddThh:mm:ss.___
)

/**
 * 일정 미루기 응답
 */
data class DelayScheduleResponse(
    val errorCode: String?,
    val message: String,
    val result: ResultOfDelaySchedule
)
data class ResultOfDelaySchedule(
    val planId: Int,
    val status: String, // "NOT_STARTED"
    val scheduledStart: String, // yyyy-MM-ddThh:mm:ss:___
    val scheduledEnd: String, // yyyy-MM-ddThh:mm:ss:___
    val delayTime: Int,
    val executeTime: Int,
    val stoppedAt: String // yyyy-MM-ddThh:mm:ss:___
)

/**
 * inProgressSchedule
 */
data class InProgressScheduleResponse(
    val planId: Int,
    val aiPlanId: Int,
    val status: String
)

/**
 * 일정 상태 완료 변경
 */
data class FinishedScheduleResponse(
    val planId: Int,
    val status: String,
    val peanutCount: Int,
    val scheduledStart: String,
    val scheduledEnd: String
)

/**
 * GET api/schedules
 */
data class ScheduleInMonthResponse(
    val errorCode: String?,
    val message: String,
    val result: List<Monthly>
)
data class Monthly(
    var date: String, // yyyy-MM-dd
    var unfinishedCount: Int
)

/**
 * 안 한 일정, 미룬 일정 조회 응답
 */
data class UnstartedDelayedScheduleResponse(
    val errorCode: String?,
    val message: String,
    val result: List<GotSchedule>
)
data class GotSchedule(
    val id: Int,
    val title: String,
    val priority: String,
    val deadline: String, // yyyy-MM-ddThh:mm:ss:___
    val scheduledStart: String,
    val daysDelayed: Int = 0
)

/**
 * 일자별 일정 조회
 */
data class GetDailyScheduleResponse(
    val errorCode: String?,
    val message: String,
    val result: ResultOfGetDailySchedule
)
data class ResultOfGetDailySchedule(
    val totalCount: Int,
    val schedules: List<DailySchedule>
)
data class DailySchedule(
    val id: Int,
    val parentTitle: String,
    val title: String,
    val startTime: String,
    val endTime: String,
    val priority: String,
    val category: String
)