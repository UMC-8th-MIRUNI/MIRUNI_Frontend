package com.example.miruni.api.model


data class HomepageResponse(
    var errorCode: String?,     // 성공이면 null 반환
    var message: String,        // 성공이면 OK 반환
    var result: HomepageResult? // 성공이면 객체 반환 실패면 null
)
data class HomepageResult(
    val userId: Int,
    val name: String,
    val profileImage: String,   //"GREEN",
    val totalCount: Int, // 오늘 남은 할 일
    val scheduledCount: Int, // 예정
    val pausedCount: Int, // 중지
    val completedCount:Int, // 완료
    val achievementRate: Int, // 진행률
    val tasks: Tasks,
    val nextTask: List<NextTask>
)
// 오늘의 일정
data class Tasks(
    val notStarted: List<TaskItem>,
    val paused: List<TaskItem>,
    val finished: List<TaskItem>
)
data class TaskItem(
    val planId: Int,
    val aiPlanId: Int,
    val category: String,   // "BASIC/AI"
    val title: String,  //"과제 제출",
    val scheduledStart: String, // "오전 4:00" 예정 시간
    val staus: String,   // "NOT_STARTED"
    val pausedAt: String? = null, // 진행 시간
    val stoppedAt: String? = null, // 중지 시간
    val reviewId: Int? = null
)
// 다가오는 다음 일정
data class NextTask(
    val planId: Int,
    val aiPlanId: Int,
    val title: String, //"opic 자격증 공부"
    val description: String, //"문제집 p.60까지 풀기"
    val startDate: String, //"2025.05.28"
    val startTime: String //"오전 9:30"
)