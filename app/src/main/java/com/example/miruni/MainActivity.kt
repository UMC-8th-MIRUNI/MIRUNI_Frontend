package com.example.miruni


import android.Manifest
import android.content.pm.PackageManager

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri

import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.miruni.databinding.ActivityMainBinding
import com.example.miruni.util.AlarmHelper
import com.example.miruni.util.PopupTimeHelper

class MainActivity : AppCompatActivity() {
    /** 변수 선언 */
    // 뷰 바인딩
    private lateinit var binding : ActivityMainBinding
    // 배너 알람
    private var isReturningFromPermissionGrant = false
    private val overlayPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (Settings.canDrawOverlays(this)) {
            checkScheduleExactAlarmPermission()
            Toast.makeText(this, "권한 허용됨", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "다른 앱 위에 표시 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
    private var popupHour = 0
    private var popupMinute = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1000
                )
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, CalendarFragment())
            .commitAllowingStateLoss()

        /** 배너 알람 설정 */
        initPopupTime()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                isReturningFromPermissionGrant = true
                requestOverlayPermission(this)
            } else {
                checkScheduleExactAlarmPermission()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let {
            when(it.getStringExtra("showFragment")) {
                "ScheduleFragment" -> {
                    transitionFragment(ScheduleFragment())
                }
                "CalendarFragment" -> {
                    transitionFragment(CalendarFragment())
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (alarmManager.canScheduleExactAlarms()) {
                AlarmHelper.initAlarm(this, popupHour, popupMinute, PopupReceiver::class.java)
            }
        }

        if (isReturningFromPermissionGrant) {
            isReturningFromPermissionGrant = false
            if (Settings.canDrawOverlays(this)) {
                checkScheduleExactAlarmPermission()
            }
        }
    }

    override fun onStop() {
        super.onStop()

        if (isReturningFromPermissionGrant) {
            isReturningFromPermissionGrant = false // 다시 초기화
            return
        }
    }

    private fun initPopupTime() {
        popupHour = 16
        popupMinute = 44

        PopupTimeHelper.savePopupTime(this, popupHour, popupMinute)
    }

    private fun requestOverlayPermission(activity: Activity) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${activity.packageName}")
        )
        overlayPermissionLauncher.launch(intent)
    }

    private fun checkScheduleExactAlarmPermission() {
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                startActivity(intent)
                return
            }
        }
        AlarmHelper.initAlarm(this, popupHour, popupMinute, PopupReceiver::class.java)
    }

    private fun transitionFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, fragment)
            .commitAllowingStateLoss()
    }
}
