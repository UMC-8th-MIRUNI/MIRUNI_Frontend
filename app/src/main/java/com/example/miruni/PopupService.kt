package com.example.miruni

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
import com.example.miruni.util.AlarmHelper
import com.example.miruni.util.NotificationHelper
import com.example.miruni.util.PopupTimeHelper

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
        val (hour, minute) = PopupTimeHelper.loadPopupTime(this)
        popupHour = hour
        popupMinute = minute
    }

    private fun setupNotification() {
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
        popupView = inflater.inflate(R.layout.layout_popup, null)

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

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "popup_service_channel"
    }
}
