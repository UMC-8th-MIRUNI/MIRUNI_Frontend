package com.example.miruni.util

import android.icu.text.DecimalFormat
import androidx.compose.runtime.rememberUpdatedState
import com.example.miruni.data.Time
import com.example.miruni.data.ampm
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.util.Calendar

fun calendarDayToStringHelper(date: CalendarDay?): String {
    if (date == null) return ""

    val numberFormat = DecimalFormat("00")
    val dateString = date.toString().substring(12, date.toString().lastIndex).split("-")
    val ymd = ArrayList<Int>()
    dateString.forEach { date ->
        ymd.add(date.toInt())
    }

    val result = String.format("${numberFormat.format(ymd[0])}-${numberFormat.format(ymd[1])}-${numberFormat.format(ymd[2])}")

    return result
}

fun calendarToDateStringHelper(calendar: Calendar): String {
    val numberFormat = DecimalFormat("00")

    val dateString = String.format("${calendar.get(Calendar.YEAR)}-${numberFormat.format(calendar.get(Calendar.MONTH) + 1)}-${numberFormat.format(calendar.get(Calendar.DAY_OF_MONTH))}")
    return dateString
}

fun timeToTimeStringHelper(time: Time): String {
    val numberFormat = DecimalFormat("00")

    val timeString = String.format("${numberFormat.format(time.hour.plus(if (time.ampm == ampm.AM) 0 else 12))}:${numberFormat.format(time.minute)}:00.000")
    return timeString
}

/**
 * yyyy-MM-ddThh:mm:ss.000 반환
 * @param dateString    "yyyy-MM-dd" 없다면 오늘 날짜
 * @param timeString    "hh:mm:ss.000" 없다면 hour, minute으로 구성
 * @param hour          시(정수) timeString이 있을 경우에는 쓸 필요 없음
 * @param minute        분(정수) timeString이 있을 경우에는 쓸 필요 없음
 */
fun getDateTimeStringHelper(
    dateString: String? = null,
    timeString: String? =null,
    hour: Int? = null,
    minute: Int? = null): String {

    val calendar = Calendar.getInstance()
    val date: String
    val time: String
    val numberFormat = DecimalFormat("00")

    if (dateString == null) {
        date = String.format("${calendar.get(Calendar.YEAR)}-${numberFormat.format(calendar.get(Calendar.MONTH) + 1)}-${numberFormat.format(calendar.get(Calendar.DAY_OF_MONTH))}")
    } else {
        date = dateString
    }

    if (timeString == null) {
        time = String.format("${numberFormat.format(hour)}:${numberFormat.format(minute)}:00.000")
    } else {
        time = timeString
    }

    val result = String.format("${date}T${time}")
    return result
}

/**
 * "yyyy-MM-ddThh:mm:ss.000"의 형식을 yyyy-MM-dd, hh:mm로 쪼개어 추출
 * @param datetimeString - 날짜 및 시간 변수
 * @param get - true: 날짜, false: 시간
 */
fun splitDateTimeHelper(datetimeString: String, getDataTime: Boolean): String {

    val dayAndTime = datetimeString.split("T")
    var result = ""
    if (getDataTime) {
        result = dayAndTime[0].toString()
    } else {
        result = dayAndTime[1].toString().substring(0, 5)
    }
    return result
}