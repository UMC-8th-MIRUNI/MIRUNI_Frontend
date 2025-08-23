package com.example.miruni.util

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import com.example.miruni.FullscreenActivity
import com.example.miruni.MainActivity
import com.example.miruni.R
import com.example.miruni.ui.calendar.ScheduleExecutionFragment

// Service: 백그라운드에서 장기 작업 실행가능
class FocusService : Service() {

    private var executedId = -1     // 실행중인 아이디 (전달용)
    private var endTime = 0L        // 시간
    private val channelId = "miruniChannel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("FocusService", "포그라운드서비스 불러와짐!!")

        executedId = intent?.getIntExtra("executedId", -1) ?: -1
        endTime = intent?.getLongExtra("endTime", 0L) ?: 0L

        // 왔다갔다 하는 시간 저장
        //val spf = getSharedPreferences("timerState", MODE_PRIVATE)
        //spf.edit().putLong("endTime", endTime).apply()

        startForeground(1, buildNotification(executedId))
        monitorApp(executedId)
        return START_STICKY
    }

    fun stopServiceManually() {
        stopForeground(true) // 알림 제거
        stopSelf()           // 서비스 종료
    }

    private fun monitorApp(executedId: Int) {
        val handler = Handler(Looper.getMainLooper())
        val notificationManager = getSystemService(NotificationManager::class.java)
        var firstForeground = false

        handler.post(object : Runnable {
            override fun run() {
                if (System.currentTimeMillis() >= endTime) {
                    stopSelf()
                    // 완료되면 어디로가는지
                    return
                }

                if (!isAppInForeground()) {
                    if(!firstForeground){
                        startForeground(1, buildNotification(executedId))
                        firstForeground = true
                    }else {
                        notificationManager.notify(1, buildNotification(executedId))
                    }
                }

                handler.postDelayed(this, 500L)
            }
        })
    }

    private fun isAppInForeground(): Boolean {
        val activityManager = getSystemService(ActivityManager::class.java)
        val runningApp = activityManager.runningAppProcesses?.firstOrNull {
            it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        }
        return runningApp?.processName == packageName
    }

    private fun buildNotification(executedId: Int) : Notification{

        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("executedId", executedId)
        intent.putExtra("endTime", endTime - System.currentTimeMillis())
        Log.d("FocusService", "MainActivity로 이동할 때: ${endTime}")
        intent.putExtra("fullBack", 100)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("앱 나가지마세요~")
            .setContentText("돌아가기 버튼을 눌러주세요")
            .setSmallIcon(R.drawable.testttttt)
            .setFullScreenIntent(pendingIntent, true)
            .build()

        return notification
    }


    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(channelId, "Focus Mode", NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    // 값이나 데이터 연결
    override fun onBind(intent: Intent?): IBinder? = null
}