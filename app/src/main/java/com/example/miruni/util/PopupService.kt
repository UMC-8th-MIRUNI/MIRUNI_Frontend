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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.example.miruni.MainActivity
import com.example.miruni.R
import com.example.miruni.TokenManager
import com.example.miruni.data.repository.HomepageRepository
import com.example.miruni.ui.homepage.HomepageViewModel
import com.example.miruni.ui.homepage.HomepageViewModelFactory

class PopupService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var popupView: View
    private lateinit var viewModel: HomepageViewModel

    override fun onCreate() {
        super.onCreate()

        // Service는 ViewModelStoreOwner가 아님 → 직접 ViewModelStoreOwner 흉내내야 함
        val repository = HomepageRepository()
        val factory = HomepageViewModelFactory(repository)

        // ViewModelStoreOwner 대신에 아래처럼 직접 관리
        viewModel = ViewModelProvider(ViewModelStore(), factory)
            .get(HomepageViewModel::class.java)

        // 데이터 불러오기 (Fragment와 동일하게)
        val token = String.format("Bearer ${TokenManager.getToken(this)}")
        viewModel.loadHomepage(token)

        setNotification()
        if (Settings.canDrawOverlays(this)) {
            // LiveData 관찰 → observeForever 사용 (Service에는 LifecycleOwner가 없음)
            viewModel.homepagedatas.observeForever { data ->
                Log.d("PopupService", "홈페이지 데이터 가져옴: $data")
                // 여기서 popupView UI 업데이트 가능
                showPopup(data.username)
            }
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
    private fun showPopup(username: String) {
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

        popupView.findViewById<TextView>(R.id.popup_title_tv).text =
            "안녕 $username! 나 미루니야!"

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
