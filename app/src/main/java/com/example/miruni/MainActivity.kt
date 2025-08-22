package com.example.miruni

import android.Manifest
import android.annotation.SuppressLint
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
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import androidx.lifecycle.lifecycleScope
import com.example.miruni.api.ApiService
import com.example.miruni.api.getRetrofit
import com.example.miruni.data.Alarm
import com.example.miruni.data.AlarmType
import com.example.miruni.data.Plan
import com.example.miruni.data.Schedule
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.data.Task
import com.example.miruni.databinding.ActivityMainBinding
import com.example.miruni.ui.calendar.CalendarFragment
import com.example.miruni.ui.calendar.ScheduleExecutionFragment
import com.example.miruni.ui.homepage.HomepageFragment
import com.example.miruni.ui.storage.StorageFragment
import com.example.miruni.ui.tool.ToolFragment
import com.example.miruni.util.AlarmHelper
import com.example.miruni.util.calendarToDateStringHelper
import com.example.miruni.util.getDateTimeStringHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    /** 변수 선언 */
    // 뷰 바인딩
    private lateinit var binding : ActivityMainBinding
    private var pageState = "home"
    private lateinit var accessToken: String
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

    // 알람 데이터 저장
    private lateinit var alarm: List<Alarm>

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        accessToken = String.format("Bearer ${TokenManager.getToken(this)}")
        setContentView(binding.root)

        /** 각 권한 확인 및 권한 설정 */
        callGetPermissionScreen()

        /** 데이터 초기화 */
        initTasksAndSchedule()
        /** Bottom Navigation 설정 */
        initBottomNavigation()
        /** Task 초기화 */
        initTasks()
//        tasksList.forEach { task ->
//            callPopupAlarm(this, task)
//            callBannerAlarm(this, task)
//        }
        /** 랜덤 팝업 */
        randomDailyPopup(this)

        /** AlarmFragment 이동 **/
        binding.mainIncludeMain.mainTopBarAlarmIv.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, AlarmFragment())
                .addToBackStack(null)
                .commit()
        }
        val fullBack = intent.getIntExtra("fullBack", -1)
        if(fullBack == 100){ // 다시 돌아왔을 때
            binding.foregroundBack.root.visibility = View.VISIBLE
            val executedId = intent.getIntExtra("executedId", -1)
            val endTime = intent.getLongExtra("endTime", 0L)

            val tag = "ScheduleExecutionFragment"
            var fragment = supportFragmentManager.findFragmentByTag(tag) as? ScheduleExecutionFragment

            if(fragment == null){
                fragment = ScheduleExecutionFragment().apply {
                    arguments = Bundle().apply {
                        putLong("endTime", endTime)
                        putInt("fullBack", fullBack)
                    }
                }
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, fragment, tag)
                    .commitAllowingStateLoss()
            }else{
                fragment.updateData(endTime, executedId)
            }
            binding.foregroundBack.foregroundBackBtn.setOnClickListener {
                val spf = this.getSharedPreferences("executedTask", MODE_PRIVATE)
                spf.edit().putInt("taskId", executedId).apply()

                binding.foregroundBack.root.visibility = View.GONE

            }
        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let {
            when(it.getStringExtra("showFragment")) {
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

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onStop() {
        super.onStop()

        tasksList.addAll(scheduleDB.taskDao().getTasks())
        tasksList.toSet().toList()

        callPopupAlarm(this, tasksList[tasksList.size-1])
        callBannerAlarm(this, tasksList[tasksList.size-1])

    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun callGetPermissionScreen() {
        val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val isNotGrantedPostNotification = ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        val isNotGrantedExactAlarmPermission = !alarmManager.canScheduleExactAlarms()
        val isNotGrantedDrawOverlay = !Settings.canDrawOverlays(this)

        if (isNotGrantedPostNotification || isNotGrantedExactAlarmPermission || isNotGrantedDrawOverlay) {
            binding.mainIncludeMain.root.visibility = View.GONE
            binding.mainIncludeGetPermission.root.visibility = View.VISIBLE

            /** '확인' 클릭 */
            binding.mainIncludeGetPermission.getPermissionOkTv.setOnClickListener {
                /**
                 * POST_NOTIFICATION 권한 설정
                 */
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
                if (!Settings.canDrawOverlays(this)) {
                    isReturningFromPermissionGrant = true
                    requestOverlayPermission(this)
                } else {
                    checkAndRequestExactAlarmPermission(this)
                }

                binding.mainIncludeMain.root.visibility = View.VISIBLE
                binding.mainIncludeGetPermission.root.visibility = View.GONE
            }

            /** 뒤로 가기 */
            binding.mainIncludeGetPermission.getPermissionBackIv.setOnClickListener {
                binding.mainIncludeMain.root.visibility = View.VISIBLE
                binding.mainIncludeGetPermission.root.visibility = View.GONE
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

                Log.d("MAIN/PERMISSION", "EXACT_ALARM_PERMISSION")

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

    }

    /**
     * Task 리스트 초기화
     */
    private fun initTasks() {
        scheduleDB = ScheduleDatabase.getInstance(this)!!
        tasksList.addAll(scheduleDB.taskDao().getTasks())

        lifecycleScope.launch {
            val calendar = Calendar.getInstance()
            loadTaskOnDate(calendarToDateStringHelper(calendar))
        }
    }

    private suspend fun loadTaskOnDate(date: String) {
        try {
            val api = getRetrofit().create(ApiService::class.java)
            Log.d("Calendar", date)
            val response = api.getDailySchedule(accessToken, date)

            if (response.isSuccessful) {
                val result = response.body()!!.result

                result.schedules.forEach { schedule ->

                    Log.d("Calendar", "schedule: ${schedule.id}" +
                            "\n${schedule.parentTitle}" +
                            "\n${schedule.title}" +
                            "\n${schedule.startTime}" +
                            "\n${schedule.endTime}" +
                            "\n${schedule.priority}" +
                            "\n${schedule.category}")
                    val task = Task(
                        id = schedule.id,
                        scheduleId = null,
                        title = schedule.title,
                        executeDay = date,
                        startTime = schedule.startTime,
                        endTime = schedule.endTime,
                        status = "undo"
                    )
                    Log.d("Calendar", "저장된 값: ${task}")

                    tasksList.add(task)
                }
            } else {
                Log.d("Calendar", "실패: ${response.code()} / ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("Calendar", "에러: ${e.message}")
        }
    }

    /**
     * 세부 일정 시작 시간 5분 초과 시 팝업 알람
     */
    // 실제 수행용
//    @RequiresApi(Build.VERSION_CODES.S)
//    private fun callPopupAlarm(context: Context, task: Task) {
//        val calendar = Calendar.getInstance().apply {
//            val hour = timeStringToIntConverter(task.startTime) / 100
//            val minute = timeStringToIntConverter(task.startTime) % 100 + 5
//
//            set(Calendar.HOUR_OF_DAY, hour)
//            set(Calendar.MINUTE, minute)
//            set(Calendar.SECOND, 0)
//            if (before(Calendar.getInstance())) add(Calendar.DATE, 1)
//        }
//
//        AlarmHelper.setAlarm(context, calendar.timeInMillis, task, AlarmHelper.AlarmType.POPUP)
//    }

    // 시연용
    @RequiresApi(Build.VERSION_CODES.S)
    private fun callPopupAlarm(context: Context, task: Task) {
        val triggerTime = System.currentTimeMillis() + 20_000

        AlarmHelper.setAlarm(context, triggerTime, task, AlarmHelper.AlarmType.POPUP)
    }

    /**
     * 세부 일정 시작 1시간, 10분 전 배너 알람 (헤드업, 상태 표시줄)
     */
    // 실제 수행용
//    @RequiresApi(Build.VERSION_CODES.S)
//    private fun callBannerAlarm(context: Context, task: Task) {
//        val hour = timeStringToIntConverter(task.startTime) / 100
//        val minute = timeStringToIntConverter(task.startTime) % 100
//
//        val baseTime = Calendar.getInstance().apply {
//            set(Calendar.HOUR_OF_DAY, hour)
//            set(Calendar.MINUTE, minute)
//            set(Calendar.SECOND, 0)
//            if (before(Calendar.getInstance())) add(Calendar.DATE, 1)
//        }
//
//        val oneHourBefore = baseTime.clone() as Calendar
//        oneHourBefore.add(Calendar.HOUR_OF_DAY, -1)
//
//        val tenMinuteBefore = baseTime.clone() as Calendar
//        tenMinuteBefore.add(Calendar.MINUTE, -10)
//
//        if (oneHourBefore.after(Calendar.getInstance())) {
//            AlarmHelper.setAlarm(context, oneHourBefore.timeInMillis, task, AlarmHelper.AlarmType.BANNER_1H)
//        }
//        if (tenMinuteBefore.after(Calendar.getInstance())) {
//            AlarmHelper.setAlarm(context, tenMinuteBefore.timeInMillis, task, AlarmHelper.AlarmType.BANNER_10M)
//        }
//    }

    // 시연용
    @RequiresApi(Build.VERSION_CODES.S)
    private fun callBannerAlarm(context: Context, task: Task) {

        val triggerTime = System.currentTimeMillis() + 30_000 // 20초 뒤

        AlarmHelper.setAlarm(context, triggerTime, task, AlarmHelper.AlarmType.BANNER_1H)
    }

    /**
     * Task의 시간 값을 "00:00" -> 0000으로 변환
     */
    private fun timeStringToIntConverter(time: String): Int {
        val hm = time.split(":")
        return String.format("${hm[0]}${hm[1]}").toInt()
    }

    /**
     * Bottom Navigation 초기화
     */
    private fun initBottomNavigation() {

        val displayMetrics = Resources.getSystem().displayMetrics
        val screenHeight = displayMetrics.heightPixels
        val targetHeight = (screenHeight * 0.075).toInt()
        binding.mainIncludeMain.mainNav.layoutParams = binding.mainIncludeMain.mainNav.layoutParams.apply {
            height = targetHeight
        }

        // 시스템 바 인셋 적용
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainIncludeMain.mainNav) { view, insets ->
            val systemBarInsets = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBarInsets.bottom) // 시스템 네비게이션 바 높이만큼 padding
            insets
        }

        // 랜딩 페이지: 홈
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomepageFragment())
            .commitAllowingStateLoss()

        // 네비게이션
        binding.mainIncludeMain.mainNavToolIv.setOnClickListener {
            trasitionScreen("tool")
            setIconColor()
        }
        binding.mainIncludeMain.mainNavCalendarIv.setOnClickListener {
            trasitionScreen("calendar")
            setIconColor()
        }
        binding.mainIncludeMain.mainNavHomeIv.setOnClickListener {
            trasitionScreen("home")
            setIconColor()
        }
        binding.mainIncludeMain.mainNavLockerIv.setOnClickListener {
            trasitionScreen("locker")
            setIconColor()
        }
        binding.mainIncludeMain.mainNavMypageIv.setOnClickListener {
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

        val dummyTask = Task(-1, 2, "랜덤 팝업","2025-08-08", "00:00", "00:00", "random")
        AlarmHelper.setAlarm(context, calendar.timeInMillis, dummyTask, AlarmHelper.AlarmType.POPUP)
    }

    /**
     * 화면 전환
     */
    @SuppressLint("SuspiciousIndentation")
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
                setTopBarColor(R.color.main)
            }
            "locker" -> {
                //transitionFragment(LockerFragment())
                //transitionFragment(MemoirListFragment())
                transitionFragment(StorageFragment())
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
                binding.mainIncludeMain.apply {
                    mainNavToolIv.setColorFilter(resources.getColor(R.color.selectColor))
                    mainNavToolTv.setTextColor("#1AE019".toColorInt())
                }
            }
            "calendar" -> {
                binding.mainIncludeMain.apply {
                    mainNavCalendarIv.setColorFilter(resources.getColor(R.color.selectColor))
                    mainNavCalendarTv.setTextColor("#1AE019".toColorInt())
                }
            }
            "locker" -> {
                binding.mainIncludeMain.apply {
                    mainNavLockerIv.setColorFilter(resources.getColor(R.color.selectColor))
                    mainNavLockerTv.setTextColor("#1AE019".toColorInt())
                }
            }
            "mypage" -> {
                binding.mainIncludeMain.apply {
                    mainNavMypageIv.setColorFilter(resources.getColor(R.color.selectColor))
                    mainNavMypageTv.setTextColor("#1AE019".toColorInt())
                }
            }
        }
    }

    /**
     * Bottom Navigation 버튼 색상 초기화
     */
    private fun initIconColor() {
        binding.mainIncludeMain.apply {
            mainNavToolIv.setColorFilter(resources.getColor(R.color.unselectColor))
            mainNavToolTv.setTextColor("#484C52".toColorInt())

            mainNavCalendarIv.setColorFilter(resources.getColor(R.color.unselectColor))
            mainNavCalendarTv.setTextColor("#484C52".toColorInt())

            mainNavLockerIv.setColorFilter(resources.getColor(R.color.unselectColor))
            mainNavLockerTv.setTextColor("#484C52".toColorInt())

            mainNavMypageIv.setColorFilter(resources.getColor(R.color.unselectColor))
            mainNavMypageTv.setTextColor("#484C52".toColorInt())
        }
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

    // topbar color 변경
    fun setTopBarColor(colorResId: Int) {
        val topBar = findViewById<View>(R.id.main_top_bar)
        topBar.setBackgroundColor(ContextCompat.getColor(this, colorResId))
    }
}
