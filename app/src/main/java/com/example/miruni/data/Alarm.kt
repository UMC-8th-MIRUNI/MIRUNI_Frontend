package com.example.miruni.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarmTable")
data class Alarm (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val content: String,
    val time: String,
    val alarmType: AlarmType
)
enum class AlarmType {
    POPUP,
    BANNER
}