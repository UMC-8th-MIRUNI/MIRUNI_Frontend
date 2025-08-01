package com.example.miruni.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TaskTable")
data class Task(
    var scheduleId: Int,
    var title: String,
    var startTime: String,
    var endTime: String,
    var status: String?
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}
