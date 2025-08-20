package com.example.miruni.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "StopTable")
data class Stop (
    @PrimaryKey
    val taskId: Int,    // 중간에 멈춘 과제 id 저장
    var stopepedAt: String  // 중간에 멈춘 시간 저장
)