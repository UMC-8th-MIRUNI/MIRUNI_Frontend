package com.example.miruni.util

import com.prolificinteractive.materialcalendarview.CalendarDay

fun DateToStringHelper(date: CalendarDay?): String {
    if (date == null) return ""

    val dateString = date.toString().substring(12, date.toString().lastIndex)
    return dateString
}