package com.example.miruni.api

/** Request Data Class */
/**
 * 일정 등록 요청 시 데이터 클래스
 * POST api/schedule
 */
data class RegisterScheduleRequest(
    val title: String, // 제목
    val deadline: String, // 마감 일시
    val scheduleStart: String, // "2025-08-14T16:19:13.711Z"
    val scheduleEnd: String,
    val priority: String,
//    val category: String, // BASIC | AI
    val description: String
)

/**
 * AI 일정 쪼개기 API
 * POST /api/schedule/{planId}/split
 */
data class SplitScheduleRequest(
    val playType: String,
    val taskRange: String,
    val detailRequest: String
)

/**
 * 일반/AI 일정 수정 - plans는 SplitSchedule 사용
 */
data class UpdateScheduleRequest(
    val title: String,
    val deadline: String,
    val taskRange: String,
    val priority: String,
    val description: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val plans: List<SplitSchedule>
)

data class DelayScheduleRequest(
    val newStartDateTime: String, // 새롭게 설정한 datetime "yyyy-MM-ddThh:mm:ss:___"
    val expectedMinutes: Int, // 예상 소요 시간
    val category: String, // AI가 쪼깬 건지, 안 쪼갠 건지
    val executeTime: Int // 수행 시간
)

/** Response Data Class */
/**
 * GET api/schedules
 */
data class ScheduleInMonthResponse(
    val errorCode: String?,
    val message: String,
    val result: Monthly?
)
data class Monthly(
    var date: String, // yyyy-MM-dd
    var scheduleCount: Int, // 일정 갯수
    var isAllDone: Boolean // 일정 수행 여부
)

/**
 * 일정 등록 요청 시 응답 데이터 클래스
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
 * AI 일정 쪼개기 응답
 * POST /api/schedule/{planId}/split
 */
data class SplitScheduleResponse(
    val errorCode: String?,
    val message: String,
    val result: List<PostSplitSchedule>
)
data class PostSplitSchedule(
    val stepOrder: Int,
    val scheduledDate: String, // "yyyy-MM-dd"
    val description: String,
    val expectedDuration: Int,
    val startTime: String, // "hh:mm:ss"
    val endTime: String // "hh:mm:ss"
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
    val title: String, // 대주제
    val deadline: String,
    val taskRange: String,
    val priority: String,
//    val category: String, // BASIC | AI
    val plans: List<SplitSchedule>
)
data class SplitSchedule( // 소주제
    val planId: Int,
    val date: String, // yyyy-MM-dd
    val description: String,
    val expectedDuration: Int,
    val scheduledStartTime: String, // hh:mm:ss
    val scheduledEndTime: String, // hh:mm:ss
    val aiDelete: Boolean?
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
 * 안 한 일정, 미룬 일정 조회 응답
 */
data class UnfinishedDelayedScheduleResponse(
    val errorCode: String?,
    val message: String,
    val result: GotSchedule
)
data class GotSchedule(
    val id: Int,
    val title: String,
    val scheduleStart: String, // yyyy-MM-ddThh:mm:ss:___
    val category: String // BASIC | AI
)

data class GetScheduleOfDayResponse(
    val errorCode: String?,
    val message: String,
    val result: ResultOfGetScheduleOfDay
)
data class ResultOfGetScheduleOfDay(
    val totalCount: Int,
    val schedules: List<SchedulesOfDay>
)
data class SchedulesOfDay(
    val id: Int,
    val parentTitle: String,
    val title: String,
    val startTime: String,
    val endTime: String,
    val priority: String,
    val category: String
)