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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.example.miruni.data.Schedule
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.data.Task
import com.example.miruni.databinding.ActivityMainBinding
import com.example.miruni.ui.calendar.CalendarFragment
import com.example.miruni.util.AlarmHelper
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    /** 변수 선언 */
    // 뷰 바인딩
    private lateinit var binding : ActivityMainBinding
    private var pageState = "home"
    // 팝업 알람 관련 변수
    private var isReturningFromPermissionGrant = false // 권한 허용 상태인지
    private val overlayPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (Settings.canDrawOverlays(this)) {
            checkAndRequestExactAlarmPermission(this)
            Toast.makeText(this, "권한 허용됨", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "다른 앱 위에 표시 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
    private var alarmAlreadyScheduled = false // 앱 설치 후 첫 실행 시 등록한 일정에 대해 알람 등록하기 위한 변수
    // 데이터 관리
    private lateinit var scheduleDB : ScheduleDatabase
    private var tasksList = arrayListOf<Task>()

    @RequiresApi(Build.VERSION_CODES.S)
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
        checkAndRequestExactAlarmPermission(this)

        /** 팝업 알람 설정 */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                isReturningFromPermissionGrant = true
                requestOverlayPermission(this)
            } else {
                checkAndRequestExactAlarmPermission(this)
            }
        }

        /** 데이터 초기화 */
        initTasksAndSchedule()
        /** Bottom Navigation 설정 */
        initBottomNavigation()
        /** Task 초기화 */
        initTasks()
        tasksList.forEach { task ->
            callPopupAlarm(this, task)
            callBannerAlarm(this, task)
        }
        /** 랜덤 팝업 */
        randomDailyPopup(this)
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

        if (tasksList.isNotEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                if (alarmManager.canScheduleExactAlarms() && !alarmAlreadyScheduled) {
                    tasksList.forEach { task ->
                        callPopupAlarm(this, task)
                        callBannerAlarm(this, task)
                    }
                    alarmAlreadyScheduled = true
                }
            }
        }

    }

    /**
     * Schedule Exact Alarm 권한 확인 및 권한 설정 화면 호출
     */
    private fun checkAndRequestExactAlarmPermission(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
            }
        }
    }

    /**
     * 임의의 더미 데이터 설정
     */
    private fun initTasksAndSchedule() {
        val scheduleDB = ScheduleDatabase.getInstance(this)!!

        val tasks = scheduleDB.taskDao().getTasks()
        val schedules = scheduleDB.scheduleDao().getSchedules()

        /** task 테이블 초기화 */
        if (tasks.isNotEmpty()) return

        scheduleDB.taskDao().insert(
            Task(
                1,
                "title1",
                "14:00",
                "16:00",
                "예정"
            )
        )
        scheduleDB.taskDao().insert(
            Task(
                1,
                "title2",
                "15:00",
                "16:00",
                "예정"
            )
        )
        scheduleDB.taskDao().insert(
            Task(
                1,
                "title3",
                "16:00",
                "17:00",
                "예정"
            )
        )
        scheduleDB.taskDao().insert(
            Task(
                2,
                "titleA",
                "14:00",
                "16:00",
                "예정"
            )
        )
        scheduleDB.taskDao().insert(
            Task(
                2,
                "titleB",
                "14:00",
                "16:00",
                "예정"
            )
        )

        /** schedule 테이블 초기화 */
        if (schedules.isNotEmpty()) return
        scheduleDB.scheduleDao().insert(
            Schedule(
                "토익 LC 공부하기",
                " ",
                "2025-07-04",
                "상"
            )
        )
        scheduleDB.scheduleDao().insert(
            Schedule(
                "토익 RC 공부하기",
                " ",
                "2025-07-05",
                "상"
            )
        )

    }

    /**
     * Task 리스트 초기화
     */
    private fun initTasks() {
        scheduleDB = ScheduleDatabase.getInstance(this)!!
        tasksList.addAll(scheduleDB.taskDao().getTasks())
    }

    /**
     * 세부 일정 시작 시간 5분 초과 시 팝업 알람
     */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun callPopupAlarm(context: Context, task: Task) {
        val calendar = Calendar.getInstance().apply {
            val hour = timeStringToIntConverter(task.startTime) / 100
            val minute = timeStringToIntConverter(task.startTime) % 100 + 5
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DATE, 1)
        }

        AlarmHelper.setAlarm(context, calendar.timeInMillis, task, AlarmHelper.AlarmType.POPUP)
    }

    /**
     * 세부 일정 시작 1시간, 10분 전 배너 알람 (헤드업, 상태 표시줄)
     */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun callBannerAlarm(context: Context, task: Task) {
        val hour = timeStringToIntConverter(task.startTime) / 100
        val minute = timeStringToIntConverter(task.startTime) % 100

        val baseTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DATE, 1)
        }

        val oneHourBefore = baseTime.clone() as Calendar
        oneHourBefore.add(Calendar.HOUR_OF_DAY, -1)

        val tenMinuteBefore = baseTime.clone() as Calendar
        tenMinuteBefore.add(Calendar.MINUTE, -10)

        if (oneHourBefore.after(Calendar.getInstance())) {
            AlarmHelper.setAlarm(context, oneHourBefore.timeInMillis, task, AlarmHelper.AlarmType.BANNER_1H)
        }
        if (tenMinuteBefore.after(Calendar.getInstance())) {
            AlarmHelper.setAlarm(context, tenMinuteBefore.timeInMillis, task, AlarmHelper.AlarmType.BANNER_10M)
        }
    }

    /**
     * Task의 시간 값을 "00:00" -> 0000으로 변환
     */
    private fun timeStringToIntConverter(time: String): Int {
        val HM = time.split(":")
        return String.format("${HM[0]}+${HM[1]}").toInt()
    }

    /**
     * Bottom Navigation 초기화
     */
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

    /**
     * 매일 랜덤 발생하는 팝업 알람
     */
    @RequiresApi(Build.VERSION_CODES.S)
    private fun randomDailyPopup(context: Context) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, (9..22).random()) // 시 랜덤
            set(Calendar.MINUTE, (0..59).random()) // 분 랜덤
            set(Calendar.SECOND, 0) // 분 단위 랜덤이므로 초는 0초로 고정
            if (before(Calendar.getInstance())) add(Calendar.DATE, 1)
        }

        val dummyTask = Task(-1, "랜덤 팝업", "00:00", "00:00", "random")
        AlarmHelper.setAlarm(context, calendar.timeInMillis, dummyTask, AlarmHelper.AlarmType.POPUP)
    }

    /**
     * 화면 전환
     */
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

    /**
     * Bottom Navigation 아이콘 색상 설정
     */
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

    /**
     * Bottom Navigation 버튼 색상 초기화
     */
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

    /**
     * 다른 앱 위에 표시 권한
     */
    private fun requestOverlayPermission(activity: Activity) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${activity.packageName}")
        )
        overlayPermissionLauncher.launch(intent)
    }

    /**
     * Fragment 전환
     */
    private fun transitionFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, fragment)
            .commitAllowingStateLoss()
    }
}
