package com.example.miruni.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "TaskTable")
data class Task(
    @PrimaryKey
    var id: Int,
    var scheduleId: Int? = null,
    var title: String,
    var executeDay: String, // yyyy-MM-dd
    var startTime: String, // hh:mm:ss
    var endTime: String, // hh:mm:ss
    var status: String?
)