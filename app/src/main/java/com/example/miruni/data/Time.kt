package com.example.miruni.data

import java.util.Calendar

data class Time(
    var date: Calendar, // 날짜
    var hour: Int, // 시
    var minute: Int, // 분
    var ampm: ampm
)

enum class ampm{
    AM,
    PM
}
