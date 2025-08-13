package com.example.miruni.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.miruni.data.Alarm
import com.example.miruni.data.AlarmType
import com.example.miruni.data.ScheduleDatabase

object NotificationHelper {
    fun notificationForPopup(service: Service, channelId: String): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "팝업 알림 채널",
                NotificationManager.IMPORTANCE_MIN
            )

            val manager = service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(service, channelId)
            .setContentTitle("")
            .setContentText("")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()

        val popupInfoIntent = Intent(service, AlarmReceiver::class.java)
        popupInfoIntent.putExtra("title", "팝업제목입니다")
        popupInfoIntent.putExtra("content", "팝업내용입니다")

        return notification
    }

    /**
     * 배너 알람 출력
     */
    fun showBannerNotification(context: Context, userName: String, content: String) {
        val channelId = "banner_channel" // 배너 알람을 위한 채널

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "배너 알림 채널",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "일정 시작 전 알림"
                enableLights(true)
            }
            context.getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)

        } else {
            TODO("VERSION.SDK_INT < O")
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // 아이콘
            .setContentTitle("안녕. $userName") // 제목
            .setContentText(content) // 내용
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}