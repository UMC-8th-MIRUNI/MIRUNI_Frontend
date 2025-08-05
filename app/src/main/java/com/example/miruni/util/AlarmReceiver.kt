package com.example.miruni.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi

class AlarmReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {

        val type = intent.getStringExtra("type") ?: return

        when (AlarmHelper.AlarmType.valueOf(type)) {
            AlarmHelper.AlarmType.POPUP -> { // 팝업 알람의 경우
                if (!isAppInForeground(context)) {
                    val serviceIntent = Intent(context, PopupService::class.java)
                    context.startForegroundService(serviceIntent)
                }
            }
            AlarmHelper.AlarmType.BANNER_1H, AlarmHelper.AlarmType.BANNER_10M -> { // 배너 알람의 경우
                if (!isAppInForeground(context)) {
                    val title = intent.getStringExtra("title") ?: "일정 알림"

                    val content = when (AlarmHelper.AlarmType.valueOf(type)) {
                        AlarmHelper.AlarmType.BANNER_1H -> String.format("1시간 뒤에 <${title}>가 예정되어 있어!")
                        AlarmHelper.AlarmType.BANNER_10M -> String.format("10분 뒤에 <${title}>가 예정되어 있어!")
                        else -> ""
                    }
                    NotificationHelper.showBannerNotification(context, title, content)
                }
            }
        }
    }

    /**
     * 현재 앱이 포그라운드 상태인지 체크
     */
    private fun isAppInForeground(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val appProcesses = activityManager.runningAppProcesses ?: return false
        val packageName = context.packageName
        return appProcesses.any {
            it.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    it.processName == packageName
        }
    }
}