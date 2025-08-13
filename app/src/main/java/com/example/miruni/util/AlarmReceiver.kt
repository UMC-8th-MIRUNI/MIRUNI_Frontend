package com.example.miruni.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.miruni.data.Alarm
import com.example.miruni.data.AlarmType
import com.example.miruni.data.ScheduleDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class   AlarmReceiver : BroadcastReceiver() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        val db = ScheduleDatabase.getInstance(context)!!
        val type = intent.getStringExtra("type") ?: return

        when (AlarmHelper.AlarmType.valueOf(type)) {
            AlarmHelper.AlarmType.POPUP -> { // 팝업 알람의 경우
                if (!isAppInForeground(context)) {
                    val serviceIntent = Intent(context, PopupService::class.java)
                    context.startForegroundService(serviceIntent)
                }
                /* NotificationHelper - fun notificationForPopup()에서 받아옴 */
                val title = intent?.getStringExtra("title") ?: "팝업 타이틀 없다"
                val content = intent?.getStringExtra("content") ?: "팝업 내용 없다"

                /* 알람 데이터 삽입 */
                GlobalScope.launch(Dispatchers.IO) {
                    db.alarmDao().insertAlarm(
                        Alarm(
                            title=title,
                            content = content,
                            time = "${System.currentTimeMillis()}",
                            alarmType = AlarmType.POPUP
                        )
                    )
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

                    /* 알람 데이터 삽입 */
                    GlobalScope.launch(Dispatchers.IO){
                        db.alarmDao().insertAlarm(
                            Alarm(
                                title = title,
                                content = content,
                                time = "${System.currentTimeMillis()}",
                                alarmType = AlarmType.BANNER
                            )
                        )
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