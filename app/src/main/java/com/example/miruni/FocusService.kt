package com.example.miruni

import android.app.ActivityManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper

// Service: 백그라운드에서 장기 작업 실행가능
class FocusService : Service() {

    private var endTime = 0L
    private val channelId = "miruniChannel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        endTime = intent?.getLongExtra("endTime", 0L) ?: 0L
        monitorApp()
        return START_STICKY
    }

    private fun monitorApp() {
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
                        startForeground(1, buildNotification())
                        firstForeground = true
                    }else {
                        notificationManager.notify(1, buildNotification())
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

    private fun buildNotification() : Notification{

        val intent = Intent(this, FullscreenActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)


        val notification = Notification.Builder(this, channelId)
            .setContentTitle("앱 나가지마세요~")
            .setContentText("돌아가기 버튼을 눌러주세요")
            .setSmallIcon(R.drawable.mypage_face)
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