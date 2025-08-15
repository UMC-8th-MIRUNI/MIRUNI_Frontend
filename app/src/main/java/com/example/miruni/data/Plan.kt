package com.example.miruni.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "PlanTable")
data class Plan(
    @PrimaryKey
    var id: Int,
    var category: String? = null,
    var parentTitle: String? = null,
    var title: String? = null,
    var deadline: String? = null, // "2025-08-14T16:19:13.711Z"
    var scheduledStart: String? = null, // "2025-08-14T16:19:13.711Z"
    var scheduledEnd: String? = null, // "2025-08-14T16:19:13.711Z"
    var expectedDuration: String? = null,
    var priority: String? = null,
    var description: String? = null,
    var planType: String? = null,
    var taskRange: String? = null,
    var detailRequest: String? = null,
    var isDone: Boolean? = null,
    var executeTime: Int? = null, // 일정 미루기 전까지 수행한 시간
    var stoppedAt: String? = null // 일정 미룬 시간 / "2025-08-14T16:19:13.711Z"
)
