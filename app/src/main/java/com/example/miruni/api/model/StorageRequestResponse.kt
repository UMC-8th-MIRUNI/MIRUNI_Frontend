package com.example.miruni.api.model

/* 보관함 정보 조회 */
data class StorageRequest(
    val year: Int,
    val month: Int
)
data class StorageResponse(
    val errorCode: String?,
    val message: String,
    val result: StorageOpen
)
data class StorageOpen(
    val peanutCount: Int,   // 사용자 땅콩 수
    val completionRatePercent: Int, // !!이번달 일정 달성률
    val isOpenedThisMonth: Boolean, // true면 '오픈하기' 대신 '이번달 리포' 버튼 보여주도록
    val canOpenThisMonth: Boolean,  // !! 이번달 리포트 오픈 조건(땅콩 ≥ 30 && 완료율 ≥ 80%) 충족 여부
    val isOpenedLastMonth: Boolean, // !! 저번달 리포트 오픈 여부
    val lockState: String,  //"잠김" or "열림"
    val isOpenButtonVisible: Boolean // !! false면 '저번달 리포트 보기' 만 보여주기 / true면 '오픈하기' 버튼
)
/* 이번달 리포트 오픈 */
data class MonthOpenResponse(
    val errorCode: String?,
    val message: String,
    val result: StorageOpenData
)

data class StorageOpenData(
    val summary: Summary,
    val delayPattern: DelayPattern,
    val emotionAchievement: EmotionAchievement,
    val averageAchievementPercent: Int,
    val simpleKeywords: List<String>,
    val selfReflections: List<String>,
    val monthOverMonthDelta: MonthOverMonthDelta
)
data class Summary(
    val totalPlans: Int,
    val completedPlans: Int,
    val completionRatePercent: Int,
    val completionRatioText: String,
    val totalExecuteTime: Int,
    val totalDelayTime: Int
)
data class DelayPattern(
    val mostDelayedTimeBand: String,
    val mostFocusedTimeBand: String,
    val delayByCategory: DelayByCategory
)
data class DelayByCategory(
    val PREPARATION_PLANNING: Int,
    val ROUTINE: Int,
    val STUDY_ORGANIZATION: Int,
    val CREATIVE: Int,
    val COLLAB_COMMUNICATION: Int,
    val IMMERSIVE: Int,
    val PRACTICAL_ADMINL: Int
)

data class EmotionAchievement(
    val moodPercents: List<String>
)
data class MonthOverMonthDelta(
    val moodPercents: List<String>
)
