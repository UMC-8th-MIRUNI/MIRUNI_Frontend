package com.example.miruni.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ScheduleTable")
data class Schedule(
    var title: String, // 대주제 이름
    var comment: String, // 한 줄 설명
    var startDate: String, // 시작 기한(=수행날짜) yyyy-mm-dd
    var endDate: String, // 종료 기한(=마감 날짜) yyyy-mm-dd
    var priority: String // 우선 순위 - HIGH, MEDIUM, LOW
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
