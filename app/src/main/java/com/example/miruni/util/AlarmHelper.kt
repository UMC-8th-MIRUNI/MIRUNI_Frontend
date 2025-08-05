package com.example.miruni.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.miruni.data.Task

object AlarmHelper {
    /**
     * 알람 설정
     * @param type 알람의 종류: 1xxxx - 팝업 | 2xxxx - 1시간 전 배너 | 3xxxx - 10분 전 배너
     */
    @RequiresApi(Build.VERSION_CODES.S)
    fun setAlarm(
        context: Context,
        triggerTimeMills: Long,
        task: Task,
        type: AlarmType
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val requestCode = when (type) {
            AlarmType.POPUP -> 10000 + task.id
            AlarmType.BANNER_1H -> 20000 + task.id
            AlarmType.BANNER_10M -> 30000 + task.id
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.example.notificationdevelop.ACTION_${requestCode}"
            putExtra("taskId", task.id)
            putExtra("title", task.title)
            putExtra("type", type.name)
            putExtra("startTime", task.startTime)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTimeMills,
                    pendingIntent
                )
            } else {
                Log.e("AlarmHelper", "권한 없음: 정확한 알람 설정 실패")
            }
        } catch (e: SecurityException) {
            Log.e("AlarmHelper", "SecurityException 발생: ${e.message}")
        }
    }

    /**
     * 알람 종류 구분
     */
    enum class AlarmType {
        POPUP, // 팝업 알람
        BANNER_1H, // 1시간 전 배너 알람
        BANNER_10M // 10분 전 배너 알람
    }
}