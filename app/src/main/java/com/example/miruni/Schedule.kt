package com.example.miruni

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="schedule_table")
data class Schedule(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String,
    var comment: String,
    var date: String,
    var priority: String
)
