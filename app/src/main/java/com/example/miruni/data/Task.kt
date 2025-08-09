package com.example.miruni.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TaskTable")
data class Task(
    var scheduleId: Int,
    var title: String,
    var executeDay: String, // yyyy-MM-dd
    var startTime: String, // hh:mm
    var endTime: String, // hh:mm
    var status: String?
) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}

