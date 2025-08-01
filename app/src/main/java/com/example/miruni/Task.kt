package com.example.miruni

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName="task_table")
data class Task(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var cheduleId : Int,
    var startTime: String,
    var endTime: String,
    var content: String,
    var status: String?
)
