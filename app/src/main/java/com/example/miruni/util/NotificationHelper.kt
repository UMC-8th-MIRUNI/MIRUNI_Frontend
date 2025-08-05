package com.example.miruni.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

object NotificationHelper {
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