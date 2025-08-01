package com.example.miruni

import androidx.room.PrimaryKey

data class HomepageResponse(
    var errorCode: String?,     // 성공이면 null 반환
    var message: String,        // 성공이면 OK 반환
    var result: HomepageResult? // 성공이면 객체 반환 실패면 null
)
data class HomepageResult(
    val user: User,
    val taskCount: Int,
    val task: List<Task>,
    val progress: Progress,
    val motivationalMessage: String
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val userName: String
)
data class Progress(
    val totalCount : Int,
    val completedCount: Int
)
