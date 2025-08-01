package com.example.miruni

import android.Manifest
import android.content.pm.PackageManager

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.example.miruni.databinding.ActivityMainBinding
import com.example.miruni.util.AlarmHelper
import com.example.miruni.util.PopupTimeHelper

class MainActivity : AppCompatActivity() {
    /** 변수 선언 */
    // 뷰 바인딩
    private lateinit var binding : ActivityMainBinding
    private var pageState = "home"
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

        initBottomNavigation()
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

    private fun initBottomNavigation() {

        val displayMetrics = Resources.getSystem().displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val targetHeight = (screenHeight * 0.075).toInt()
        binding.mainNav.layoutParams = binding.mainNav.layoutParams.apply {
            height = targetHeight
        }

        // 랜딩 페이지: 홈
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomepageFragment())
            .commitAllowingStateLoss()

        // 네비게이션
        binding.navToolIv.setOnClickListener {
            trasitionScreen("tool")
            setIconColor()
        }
        binding.navCalendarIv.setOnClickListener {
            trasitionScreen("calendar")
            setIconColor()
        }
        binding.navHomeIv.setOnClickListener {
            trasitionScreen("home")
            setIconColor()
        }
        binding.navLockerIv.setOnClickListener {
            trasitionScreen("locker")
            setIconColor()
        }
        binding.navMypageIv.setOnClickListener {
            trasitionScreen("mypage")
            setIconColor()
        }
    }

    private fun trasitionScreen(pageState: String) {
        this.pageState = pageState
        when(pageState) {
            "tool" -> {
                transitionFragment(ToolFragment())
            }
            "calendar" -> {
                transitionFragment(CalendarFragment())
            }
            "home" -> {
                transitionFragment(HomepageFragment())
            }
            "locker" -> {
                transitionFragment(LockerFragment())

                // 확인용 코드
                val intent = Intent(this, ProcessingActivity::class.java)
                intent.putExtra("showFragment", "MemoirAddFragment")
                startActivity(intent)

            }
            "mypage" -> {
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.main_frm, MyPageFragment())
//                    .commitAllowingStateLoss()
                val intent = Intent(this, MyPageActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun setIconColor() {
        initIconColor()

        when (pageState) {
            "tool" -> {
                binding.apply {
                    navToolIv.setColorFilter(resources.getColor(R.color.selectColor))
                    navToolTv.setTextColor("#1AE019".toColorInt())
                }
            }
            "calendar" -> {
                binding.apply {
                    navCalendarIv.setColorFilter(resources.getColor(R.color.selectColor))
                    navCalendarTv.setTextColor("#1AE019".toColorInt())
                }
            }
            "home" -> {
                binding.apply {
                    navHomeTv.setTextColor("#1AE019".toColorInt())
                }
            }
            "locker" -> {
                binding.apply {
                    navLockerIv.setColorFilter(resources.getColor(R.color.selectColor))
                    navLockerTv.setTextColor("#1AE019".toColorInt())
                }
            }
            "mypage" -> {
                binding.apply {
                    navMypageIv.setColorFilter(resources.getColor(R.color.selectColor))
                    navMypageTv.setTextColor("#1AE019".toColorInt())
                }
            }
        }
    }

    private fun initIconColor() {
        binding.navToolIv.setColorFilter(resources.getColor(R.color.unselectColor))
        binding.navToolTv.setTextColor("#484C52".toColorInt())

        binding.navCalendarIv.setColorFilter(resources.getColor(R.color.unselectColor))
        binding.navCalendarTv.setTextColor("#484C52".toColorInt())

        binding.navHomeTv.setTextColor("#484C52".toColorInt())

        binding.navLockerIv.setColorFilter(resources.getColor(R.color.unselectColor))
        binding.navLockerTv.setTextColor("#484C52".toColorInt())

        binding.navMypageIv.setColorFilter(resources.getColor(R.color.unselectColor))
        binding.navMypageTv.setTextColor("#484C52".toColorInt())
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
