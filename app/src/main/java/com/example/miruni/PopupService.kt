package com.example.miruni

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.miruni.databinding.PopupLayoutBinding
import com.example.miruni.util.AlarmHelper
import com.example.miruni.util.NotificationHelper
import com.example.miruni.util.PopupTimeHelper
import java.util.Calendar

class PopupService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var popupView: View
    private var popupHour = 0
    private var popupMinute = 0

    override fun onCreate() {
        super.onCreate()

        initPopupTime()
        setupNotification()
        showPopup()
        AlarmHelper.initAlarm(this, popupHour, popupMinute, PopupReceiver::class.java)
//        scheduleNextDayBanner()
    }

    override fun onDestroy() {
        super.onDestroy()
        try{
            if (::popupView.isInitialized) {
                windowManager.removeView(popupView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun initPopupTime() {
//        val spf = getSharedPreferences("popup", MODE_PRIVATE)
//        popupHour = spf.getInt("popupHour", 9)
//        popupMinute = spf.getInt("popupMinute", 0)

        val (hour, minute) = PopupTimeHelper.loadPopupTime(this)
        popupHour = hour
        popupMinute = minute
    }

    private fun setupNotification() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                CHANNEL_ID,
//                "팝업 알림 채널",
//                NotificationManager.IMPORTANCE_LOW
//            )
//            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
//
//            val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("팝업 예정됨")
//                .setContentText("팝업이 잠시 후 나타납니다.")
//                .setSmallIcon(android.R.drawable.ic_dialog_info)
//                .build()
//
//            startForeground(NOTIFICATION_ID, notification)
//        }
        val notification = NotificationHelper.createForegroundNotification(
            this,
            CHANNEL_ID,
            "팝업 예정됨",
            "팝업이 잠시 후 나타납니다."
        )
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun showPopup() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        popupView = inflater.inflate(R.layout.popup_layout, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.CENTER

        val popupYes = popupView.findViewById<TextView>(R.id.popup_yes_tv)
        val popupNo = popupView.findViewById<TextView>(R.id.popup_no_tv)

        popupYes.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            removePopup()
        }
        popupNo.setOnClickListener {
            removePopup()
        }

        windowManager.addView(popupView, params)
    }

    private fun removePopup() {
        if (::popupView.isInitialized) {
            windowManager.removeView(popupView)
        }
        stopSelf()
    }

//    @SuppressLint("ScheduleExactAlarm")
//    private fun scheduleNextDayBanner() {
//        val intent = Intent(this, PopupReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
//
//        val calendar = Calendar.getInstance().apply {
//            add(Calendar.DATE, 1)
//            set(Calendar.HOUR_OF_DAY, popupHour)
//            set(Calendar.MINUTE, popupMinute)
//            set(Calendar.SECOND, 0)
//        }
//
//        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
//        alarmManager.setExactAndAllowWhileIdle(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            pendingIntent
//        )
//    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "popup_service_channel"
    }
}