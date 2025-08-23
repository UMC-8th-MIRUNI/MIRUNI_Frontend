package com.example.miruni.ui.calendar

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.fragment.app.replace
import com.example.miruni.MainActivity
import com.example.miruni.R
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.data.Stop
import com.example.miruni.data.Task
import com.example.miruni.databinding.FragmentScheduleExecutionBinding
import com.example.miruni.databinding.LayoutPopupScheduleDelayBinding
import com.example.miruni.databinding.LayoutScheduleDelayAmpmBinding
import com.example.miruni.databinding.LayoutScheduleDelayCalendarBinding
import com.example.miruni.ui.homepage.HomepageFragment
import com.example.miruni.util.FocusService
import com.example.miruni.util.controlBottomNavigation
import com.example.miruni.util.controlTopBar
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.format.TitleFormatter
import java.util.Calendar

class ScheduleExecutionFragment : Fragment() {

    private lateinit var binding: FragmentScheduleExecutionBinding
    private lateinit var screenState: String
    // 타이머
    private lateinit var countDownTimer: CountDownTimer
    private var timerRunning = true // 타이머 동작 여부
    private var isRunFirst = true // 처음 실행 여부
    private lateinit var executedTask: Task // 수행할 Task
    var tempTime = 0L // 타이머 일시 정지 시간
    private lateinit var db : ScheduleDatabase  // db생성
    private var blockCheck = false  // 방해금지 설정 체크 유무

    private var endTime = 0L
    private var taskId = -1

    private val hoursOnDropdown = (1..12).toList()
    private val minutesOnDropdown = (0..59).toList()
    private val ampmOnDropdown = listOf("오전", "오후")
    private var selectedHour: Int? = null
    private var selectedMinute: Int? = null
    private var selectedAmPm: String? = null
    private var selectedDate: CalendarDay? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleExecutionBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controlTopBar(context as MainActivity, false)
        controlBottomNavigation(context as MainActivity, false)

        db = ScheduleDatabase.getInstance(requireContext())!!
        blockCheck = arguments?.getBoolean("blockCheck") ?: true

        if(arguments?.getInt("fullBack") == 100) {
            retoreTimer()
            /*requireActivity().supportFragmentManager.beginTransaction()
                .remove(this@ScheduleExecutionFragment)
                .commit()*/
//            requireActivity().supportFragmentManager.popBackStack()
            Log.d("접속 확인", "포그라운에서 다시 들어옴")
        }else {
            initExecution()
            Log.d("접속 확인", "그냥 들어옴")
        }
        initExecutionFinish()
    }

    fun updateData(endTime: Long, executedId: Int) {
        this.endTime = endTime
        taskId = executedId

        retoreTimer()
    }

    /* 되돌아왔을 때 시간 받고 다시 taskId 구분하고 타이머 시작 */
    private fun retoreTimer(){
        endTime = arguments?.getLong("endTime") ?: 0L
        Log.d("FocusService", "다시 실행중으로 돌아왔을 때 돌아온 시간: ${endTime}")
        val spf = requireContext().getSharedPreferences("executedTask", MODE_PRIVATE)
        taskId = spf.getInt("taskId", -1)

        // FocusService가 가지고있는 시간 기반 남은 시간 계산
        tempTime = endTime
        if (taskId != -1) {
            val scheduleDB = ScheduleDatabase.getInstance(requireContext())!!
            executedTask = scheduleDB.taskDao().getTask(taskId)
            Log.d("FocusService", "아이디있는거")
            startTimer(tempTime)
        }else{
            Log.d("FocusService", "아이디없는거")
            startNoTaskIdTimer(tempTime)
        }
    }

    /**
     * 일정 진행중 화면
     */
    private fun initExecution() {
        val spf = requireContext().getSharedPreferences("executedTask", MODE_PRIVATE)
        val taskId = spf.getInt("taskId", -1)

        val scheduleDB = ScheduleDatabase.getInstance(requireContext())

        Log.d("FocusService", "방해금지: ${arguments?.getBoolean("blockCheck")}")

        /* 도구 페이지에서 실행하면 taskId가 없음 */
        if(taskId == -1){
            Log.d("FLOW/Execution", "taskId == -1")
            tempTime = arguments?.getLong("timer") ?: 0L
            tempTime *= 1000L
            startNoTaskIdTimer(tempTime)
        }else{
            Log.d("ScheduleExecutionFragment", "taskId: ${taskId}")
            Log.d("ScheduleExecutionFragment", "${scheduleDB!!.taskDao().getTask(taskId)}")
            executedTask = scheduleDB!!.taskDao().getTask(taskId)

            Log.d("확인", "task_id: ${executedTask.id} | task_scheduleid: ${executedTask.scheduleId}")
            if(executedTask != null){
                startTimer(tempTime)
            }
        }


        /**
         * 여기서 중지 시간 저장해야됨 taskId랑 String데이터 필요
         **/
        binding.scheduleExecutionInclude.apply {
            /** 중지 버튼 */
            scheduleExecutionStopTv.setOnClickListener {
                if(taskId == -1 ){  // 도구페이지에서 시작
                    timerStartStop()
                }else{
                    screenState = "stop"
                    timerStartStop()
                    changeLayout(binding.scheduleExecutionInclude.root, binding.scheduleStopCompleteInclude.root)
                    initExecutionStopnComplete(screenState)
                }
            }
            /** 완료 버튼 */
            scheduleExecutionCompleteTv.setOnClickListener {
                if(taskId == -1 ){
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomepageFragment())
                        .commit()
                }else {
                    screenState = "complete"
                    timerStartStop()
                    changeLayout(
                        binding.scheduleExecutionInclude.root,
                        binding.scheduleStopCompleteInclude.root
                    )
                    initExecutionStopnComplete(screenState)
                }

                /*  포그라운드 서비스 중지  */
                val intent = Intent(requireContext(), FocusService::class.java)
                requireContext().startForegroundService(intent)
                requireContext().stopService(intent)
            }
        }
    }

    /* 포그라운드 서비스 실행 */
    private fun startFocusService(executedId: Int) {
        // blockCheck가 true일 때만 실행
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // 절대시간(ms) 계산: 현재 시간 + 남은 타이머
            val endTime = System.currentTimeMillis() + tempTime

            val intent = Intent(requireContext(), FocusService::class.java)
            intent.putExtra("endTime", endTime)
            intent.putExtra("executedId", executedId)
            // apply 밖에서 안전하게 호출
            requireContext().startForegroundService(intent)
            Log.d("FocusServiceTest", "FocusService 시작 호출: endTime=$endTime")
        }
    }


    /**
     * 일정 진행중 - 중지 및 완료 화면
     */
    private fun initExecutionStopnComplete(state: String) {
        initPeanut()

        binding.scheduleStopCompleteInclude.apply {
            if (state == "stop") {
                /** 중지 화면 */
                /** 취소 버튼 */
                scheduleExecutionScCancelTv.setOnClickListener {
                    changeLayout(binding.scheduleStopCompleteInclude.root, binding.scheduleExecutionInclude.root)
                    timerStartStop()
                }
                /** 확인 버튼 */
                scheduleExecutionScCheckTv.setOnClickListener {
                    showScheduleDelayPopup(scheduleExecutionScTitleTv)
                }
            } else {
                /** 완료 화면 */
                scheduleExecutionScTxt1Tv.text = "벌써 다 끝내셨나요?"
                /** 취소 버튼 */
                scheduleExecutionScCancelTv.setOnClickListener {
                    changeLayout(binding.scheduleStopCompleteInclude.root, binding.scheduleExecutionInclude.root)
                    timerStartStop()
                }
                /** 확인 버튼 */
                scheduleExecutionScCheckTv.setOnClickListener {
                    /** 일정 진행 완료로 이동 -> 서버 송신 있는지 확인 **/

                    changeLayout(binding.scheduleStopCompleteInclude.root, binding.scheduleExecutionFinishInclude.root)

                    var hour: Int = 0
                    var min: Int = 0
                    timeCalculate(
                        resultHour = {h -> hour = h},
                        resultMinute = {m -> min = m}
                    )

                    binding.scheduleExecutionFinishInclude.scheduleExecutionFinishTimeTv.text = String.format("${hour}:${min}")

                }
            }
        }
    }

    /**
     * 일정 진행 완료
     */
    private fun initExecutionFinish() {
        binding.scheduleExecutionFinishInclude.apply {

            scheduleExecutionFinishCheckTv.setOnClickListener {
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, HomepageFragment())
                    .commitAllowingStateLoss()

                controlTopBar(context as MainActivity, true)
                controlBottomNavigation(context as MainActivity, true)
            }
        }
    }

    /**
     * 화면 전환
     */
    private fun changeLayout(
        origin: View,
        target: View
    ) {
        origin.visibility = View.GONE
        target.visibility = View.VISIBLE
    }

    /**
     * 땅콩 갯수 안내
     */
    private fun initPeanut() {
        val textData: String = binding.scheduleStopCompleteInclude.scheduleExecutionScTxt2Tv.text.toString()
        val spannableStringBuilder = SpannableStringBuilder(textData)

        val startIdx: Int = textData.indexOf("1")
        val endIdx: Int = textData.indexOf("개")
        val colorSpan = ForegroundColorSpan("#06B600".toColorInt())
        spannableStringBuilder.setSpan(colorSpan, startIdx, endIdx, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.scheduleStopCompleteInclude.scheduleExecutionScTxt2Tv.text = spannableStringBuilder
    }

    /**
     * 미루는 날짜 선택 팝업 출력
     */
    private fun showScheduleDelayPopup(anchor: View) {
        val displayMetrics = requireContext().resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val nowTime = Calendar.getInstance()
        val initAmpm = if (nowTime.get(Calendar.HOUR_OF_DAY) > 11) "오후" else "오전"

        val dropdownView = LayoutPopupScheduleDelayBinding.inflate(layoutInflater)
        dropdownView.popupScheduleDelaySelectTv.text =
            String.format("${nowTime.get(Calendar.YEAR)}." +
                    "${nowTime.get(Calendar.MONTH) + 1}." +
                    "${nowTime.get(Calendar.DAY_OF_MONTH)}. " +
                    "${nowTime.get(Calendar.HOUR)}:${nowTime.get(Calendar.MINUTE)} " +
                    "$initAmpm")

        val stopPopup = PopupWindow(
            dropdownView.root,
            (screenWidth * 0.92).toInt(),
            (screenHeight * 0.42).toInt(),
            true
        )

        stopPopup.elevation = 5f
        stopPopup.isOutsideTouchable = true

        /** 선택한 미루는 날짜 확인 창 */
        dropdownView.apply {
            popupScheduleDelayCancelTv.setOnClickListener {
                stopPopup.dismiss()
            }
            popupScheduleDelaySelectFrm.setOnClickListener {
                showScheduleDelayCalendarPopup(it) {
                    popupScheduleDelaySelectTv.text = String.format("${it.get(Calendar.YEAR)}." +
                            "${it.get(Calendar.MONTH) + 1}." +
                            "${it.get(Calendar.DAY_OF_MONTH)}. " +
                            "${it.get(Calendar.HOUR)}:${it.get(Calendar.MINUTE)} " +
                            "${selectedAmPm ?: initAmpm}")
                }
            }
            popupScheduleDelayOkTv.setOnClickListener {
                /* Stop 저장하기 */
                val hour = tempTime / 3600000
                val min = (tempTime % 3600000) / 6000
                db.stopDao().insertStop(Stop(executedTask.id, String.format("%02d:%02d", hour, min)))
                stopPopup.dismiss()

                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, CalendarFragment())
                    .commitAllowingStateLoss()
            }
        }
        stopPopup.showAsDropDown(anchor)
    }

    /**
     * 미루는 날짜 고르는 캘린더 출력
     */
    private fun showScheduleDelayCalendarPopup(
        anchor: View,
        onItemSelected: (Calendar) -> Unit) {
        val displayMetrics = requireContext().resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val dropdownView = LayoutScheduleDelayCalendarBinding.inflate(layoutInflater)
        // 달력 헤더
        dropdownView.scheduleDelayCalendarCalendar.setTitleFormatter(object : TitleFormatter {
            override fun format(day: CalendarDay?): CharSequence {
                return "${day!!.month}월 ${day.year}"
            }
        })
        // ui 초기화
        val calendar = Calendar.getInstance()
        dropdownView.scheduleDelayCalendarTimeHourTv.text = if (selectedHour != null) selectedHour.toString() else calendar.get(Calendar.HOUR).toString()
        dropdownView.scheduleDelayCalendarTimeMinuteTv.text = if (selectedMinute != null) selectedMinute.toString() else calendar.get(Calendar.MINUTE).toString()
        dropdownView.scheduleDelayCalendarTimeAmpmTv.text = if (selectedAmPm != null) selectedAmPm.toString() else "오전"

        val calendarPopup = PopupWindow(
            dropdownView.root,
            (screenWidth * 0.68).toInt(),
            (screenHeight * 0.41).toInt(),
            true
        )

        calendarPopup.elevation = 5f
        calendarPopup.isOutsideTouchable = true

        /** 각 시간대 선택 */
        dropdownView.apply {
            scheduleDelayCalendarTimeHour.setOnClickListener {
                initDelayTimeDropdown(it, "hour") {
                    scheduleDelayCalendarTimeHourTv.text = it.toString()
                }
            }
            scheduleDelayCalendarTimeMinute.setOnClickListener {
                initDelayTimeDropdown(it, "minute") {
                    scheduleDelayCalendarTimeMinuteTv.text = it.toString()
                }
            }
            scheduleDelayCalendarTimeAmpm.setOnClickListener {
                initDelayTimeAmpmDropdown(it) {
                    selectedAmPm = ampmOnDropdown[it]
                    scheduleDelayCalendarTimeAmpmTv.text = selectedAmPm
                }
            }
            scheduleDelayCalendarCalendar.setOnDateChangedListener { widget, date, selected ->
                selectedDate = date
                Log.d("selected", selectedDate.toString())
            }
            scheduleDelayCalendarOkTv.setOnClickListener {
                if (selectedDate != null && selectedHour != null && selectedMinute != null && selectedAmPm != null) {
                    val calendar = Calendar.getInstance()
                    calendar.set(
                        selectedDate!!.year,
                        selectedDate!!.month - 1,
                        selectedDate!!.day,
                        selectedHour!!,
                        selectedMinute!!
                    )
                    onItemSelected(calendar)
                } else {
                    // 기본값 세팅 (현재 시간)
                    val calendar = Calendar.getInstance()
                    onItemSelected(calendar)
                }

                calendarPopup.dismiss()
            }
        }

        calendarPopup.showAsDropDown(anchor)
    }

    /**
     * 캘린더 팝업에서 시간 선택할 수 있는 드롭다운
     */
    private fun initDelayTimeDropdown(
        anchor: View,
        tag: String,
        onItemSelected: (Int) -> Unit) {

        val inflater =  LayoutInflater.from(context as MainActivity)
        val dateSelectDropdownView = inflater.inflate(R.layout.layout_schedule_delay_date_dropdown, null)

        val dateSelectDropdown = PopupWindow(
            dateSelectDropdownView,
            anchor.width,
            LayoutParams.WRAP_CONTENT,
            true
        )

        val timeForSelectList = dateSelectDropdownView.findViewById<LinearLayout>(R.id.calendar_dropdown_dateList)

        fun updateUI(list: LinearLayout, items: List<Int>, selected: Int?, onClick: (Int) -> Unit) {
            list.removeAllViews()
            for (item in items) {
                val isSelected = item == selected
                val textView = TextView(context as MainActivity).apply {
                    text = "$item"
                    textSize = 10f
                    setTypeface(
                        ResourcesCompat.getFont(context as MainActivity,
                            R.font.poppins_regular
                        ))
                    setBackgroundColor(if (isSelected) "#F1F5F9".toColorInt() else Color.TRANSPARENT)
                    setOnClickListener {
                        onClick(item)
                    }
                }
                list.addView(textView)
            }
        }

        when (tag) {
            "hour" -> {
                updateUI(timeForSelectList, hoursOnDropdown, selectedHour) { hour ->
                    selectedHour = hour
                    onItemSelected(hour)
                    updateUI(timeForSelectList, hoursOnDropdown, selectedHour) {}
                    dateSelectDropdown.dismiss()
                }
            }

            "minute" -> {
                updateUI(timeForSelectList, minutesOnDropdown, selectedMinute) { minute ->
                    selectedMinute = minute
                    onItemSelected(minute)
                    updateUI(timeForSelectList, minutesOnDropdown, selectedMinute) {}
                    dateSelectDropdown.dismiss()
                }
            }
        }

        dateSelectDropdown.apply {
            elevation = 8f
            setBackgroundDrawable(Color.WHITE.toDrawable())
            isOutsideTouchable = true
            showAsDropDown(anchor)
        }
    }

    /**
     * 오전, 오후 선택
     */
    private fun initDelayTimeAmpmDropdown(
        anchor: View,
        onItemSelected: (Int) -> Unit) {

        val dropdownView = LayoutScheduleDelayAmpmBinding.inflate(layoutInflater)
        val ampmDropdown = PopupWindow(
            dropdownView.root,
            anchor.width,
            LayoutParams.WRAP_CONTENT,
            true
        )

        ampmDropdown.elevation = 5f
        ampmDropdown.setBackgroundDrawable(Color.WHITE.toDrawable())
        ampmDropdown.isOutsideTouchable = true

        val viewList = listOf(
            dropdownView.scheduleDelayCalendarAm,
            dropdownView.scheduleDelayCalendarPm
        )

        viewList.forEachIndexed() { index, item ->
            item.setOnClickListener {
                onItemSelected(index)
                ampmDropdown.dismiss()
            }
        }

        ampmDropdown.showAsDropDown(anchor)
    }

    /**
     * 타이머 실행 및 정지 관리
     */
    private fun timerStartStop() {
        if (timerRunning) {
            // 타이머 작동 중
            stopTimer()
        } else {
            // 타이머 미작동 중
            startTimer(tempTime)
        }
    }

    /**
     * 타이머 정지
     */
    private fun stopTimer() {
        countDownTimer.cancel() // 정지
        timerRunning = false // 상태 변경
    }

    /**
     * 타이머 실행
     */
    private fun startTimer(time: Long) {
        if (isRunFirst) {
            val startHourMinuteList = executedTask.startTime.split(":")
            val endHourMinuteList = executedTask.endTime.split(":")

            val setHour = endHourMinuteList[0].toLong() - startHourMinuteList[0].toLong()
            val setMinute = endHourMinuteList[1].toLong() - startHourMinuteList[1].toLong()

            tempTime = (setHour * 3600000) + (setMinute * 60000) + 1000
        }
        countDownTimer = object : CountDownTimer(tempTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tempTime = millisUntilFinished
                updateTimer()
            }
            override fun onFinish() {}
        }.start()

        timerRunning = true
        isRunFirst = false

        // 포그라운드 서비스 시작
        if(arguments?.getBoolean("blockCheck") == true)
            startFocusService(executedId = executedTask.id)
    }


    /*  taskId 없는 타이머 실행  */
    private fun startNoTaskIdTimer(time: Long) {
        if (isRunFirst) {
            tempTime = time

            countDownTimer = object : CountDownTimer(tempTime, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    tempTime = millisUntilFinished
                    updateTimer()
                }
                override fun onFinish() {}
            }.start()

            timerRunning = true
            isRunFirst = false

            // 포그라운드 서비스 시작
            startFocusService(-1)
        }


    }

    /**
     * 타이머 텍스트 업데이트
     */
    private fun updateTimer() {
        val hour = tempTime / 3600000
        val min = tempTime % 3600000 / 60000
        val sec = tempTime % 60000 / 1000
        if(hour < 1){
            binding.scheduleExecutionInclude.scheduleExecutionTimeTv.text = String.format("%02d:%02d",min,sec)
        }else
            binding.scheduleExecutionInclude.scheduleExecutionTimeTv.text = String.format("%02d:%02d",hour,min)
    }

    /**
     * 스톱워치 형식으로 변환
     */
    private fun timeCalculate(
        resultHour: (Int) -> Unit,
        resultMinute: (Int) -> Unit) {

        val setEndTimeList = executedTask.endTime.split(":")
        val setStartTimeList = executedTask.startTime.split(":")

        var setHour = setEndTimeList[0].toInt() - setStartTimeList[0].toInt()
        var setMinute = setEndTimeList[1].toInt() - setStartTimeList[1].toInt()

        var calHour: Long = 0
        var calMinute: Long = 0

        if (setMinute > 0) {
            calHour = setHour - (tempTime / 3600000)
            calMinute = setMinute - (tempTime % 3600000 / 60000)
        } else {
            if (setHour > 0) {
                setHour -= 1
                setMinute = 60

                calHour = setHour - (tempTime / 3600000)
                calMinute = setMinute - (tempTime % 3600000 / 60000)
            } else {
                calHour = 0
                calMinute = 0
            }
        }

        resultHour(calHour.toInt())
        resultMinute(calMinute.toInt())
    }

}