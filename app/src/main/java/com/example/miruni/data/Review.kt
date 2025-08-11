package com.example.miruni.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ReviewTable")
data class Review (
    @PrimaryKey
    val id: Int,
    val aiPlanId: Int?, // ai 아이디(일정 외래키)
    val planId: Int,    // 일정 id
    val mood: Mood,     // 기분
    val title: String,  //  일정 title
    val description: String,    // 한 줄 평
    val achievement: Int,   // 성취도
    val memo: String,   // 회고 메모
    val createdAt: String   //  2025-07-09T17:25:42.642106"
)

