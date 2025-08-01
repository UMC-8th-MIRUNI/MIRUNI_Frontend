package com.example.miruni.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ScheduleTable")
data class Schedule(
    var title: String, // 대주제 이름
    var comment: String, // 한 줄 설명
    var date: String, // 마감 기한 yyyy-mm-dd
    var priority: String // 우선 순위
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
