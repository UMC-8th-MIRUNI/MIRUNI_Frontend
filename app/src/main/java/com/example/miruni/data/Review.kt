package com.example.miruni.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ReviewTable")
data class Review (
    @PrimaryKey
    val id: Int,
    val aiPlanId: Int?,
    val planId: Int,
    val mood: Mood,
    val title: String,
    val achievement: Int,
    val memo: String,
    val createdAt: String
)
