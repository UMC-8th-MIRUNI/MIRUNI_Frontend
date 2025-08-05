package com.example.miruni.util

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.example.miruni.MainActivity
import com.example.miruni.R

class PopupService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var popupView: View

    override fun onCreate() {
        super.onCreate()

        setNotification()
        if (Settings.canDrawOverlays(this)) {
            showPopup()
        } else {
            Log.e("PopupService", "SYSTEM_ALERT_WINDOW 권한이 없음. 팝업을 띄우지 않음.")
        }
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

    /**
     * 팝업창 호출
     */
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

    /**
     * 팝업창 삭제
     */
    private fun removePopup() {
        if (::popupView.isInitialized) {
            windowManager.removeView(popupView)
        }
        stopSelf()
    }

    /**
     * 팝업을 foreground service로 호출할 때 반드시 notification이 필요함
     */
    private fun setNotification() {
        val notification = NotificationHelper.notificationForPopup(
            this,
            CHANNEL_ID
        )
        startForeground(NOTIFICATION_ID, notification)
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "popup_service_channel"
    }
}
