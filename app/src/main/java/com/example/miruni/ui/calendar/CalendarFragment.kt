package com.example.miruni.ui.calendar

import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miruni.databinding.FragmentCalendarBinding
import androidx.core.graphics.toColorInt
import com.prolificinteractive.materialcalendarview.CalendarDay
import androidx.core.graphics.drawable.toDrawable
import com.example.miruni.MainActivity
import com.example.miruni.R
import com.example.miruni.data.Schedule
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.data.Task
import com.example.miruni.util.DateToStringHelper
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import java.util.Calendar
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import com.example.miruni.api.ApiService
import com.example.miruni.api.Monthly
import com.example.miruni.api.getRetrofit
import com.example.miruni.ui.homepage.t
import com.example.miruni.util.controlBottomNavigation
import com.example.miruni.util.controlTopBar
import kotlinx.coroutines.launch

class CalendarFragment : Fragment() {
    /** 전역 변수 */
    // 뷰 바인딩
    private lateinit var binding : FragmentCalendarBinding

    // 데이터 관리
    private lateinit var scheduleDB: ScheduleDatabase
    private var taskOnDateList = ArrayList<Task>()
    private lateinit var taskOnDateRVAdapter: TaskOnDateRVAdapter
    private var monthly = ArrayList<Monthly>()

    // 캘린더
    private var YMList = arrayOf(0, 0)
    private var currentSelectionDecorator: SelectionDecorator? = null

    // 날짜 선택 드롭다운
    private lateinit var dropdownPopup: PopupWindow
    private var selectedYearOnDropdown: Int? = null
    private var selectedMonthOnDropdown: Int? = null
    private val yearsOnDropdown = (2020..2040).toList()
    private val monthsOnDropdown = (1..12).toList()

    // 날짜 선택 추적 변수
    private var selectedDate: CalendarDay? = null
    private var dateSelectState = "unselected"
    val dayOfWeekList = listOf("일", "월", "화", "수", "목", "금", "토")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCalendarBinding.inflate(layoutInflater, container, false)
        scheduleDB = ScheduleDatabase.getInstance(requireContext())!!

        /** 상단바 색상 변경 **/
        (activity as? MainActivity)?.setTopBarColor(R.color.white)

        /** 바텀 네비게이션 설정 */
        controlTopBar(context as MainActivity, true)
        controlBottomNavigation(context as MainActivity, true)

        /** 캘린더 관련 설정 */
        initCalendar()
        initTaskOnDateRVAdapter()
        initClickListener()
        initDelayedRV()

        return binding.root
    }

    /**
     * 서버에서 Task 로드
     */
    private suspend fun loadTasks(year: Int, month: Int) {

        try {
            val token = "Bearer $t"
            val api = getRetrofit().create(ApiService::class.java)
            val response = api.getScheduleInMonth(token, year, month)

            Log.d("월별 조회 결과", "성공: $response")

            // 정보 받아서 저장
            if(response.isSuccessful){
                monthly = (response.body()!!.result as ArrayList<Monthly>)

                Log.d("월별 조회 결과", "mothly-size: ${monthly.size}")
                Log.d("월별 조회 결과", "성공: ${response.body()}")

                loadDecorators()
            }else{
                Log.e("월별 조회 결과", "reponse실패: ${response.code()} - ${response.message()}")
            }
        }catch (e: Exception){
            Log.e("월별 조회 결과", "에러: ${e.message}")
        }

    }

    /**
     * 달력 초기화
     */
    private fun initCalendar() {
        val today = binding.calendarIncludeCalendarCalendar.calendarCalendar.currentDate
        selectedDate = CalendarDay.today()
        YMList[0] = today.year.toInt()
        YMList[1] = today.month.toInt()

        Log.d("Calendar:today", "YMList = ${YMList[0]}년 ${YMList[1]}월")
        lifecycleScope.launch {
            loadTasks(YMList[0], YMList[1])
        }

        val monthArray = resources.getStringArray(R.array.monthArr)
        binding.calendarIncludeCalendarCalendar.apply {
            calendarYearTv.text = String.format(YMList[0].toString()+"년")
            calendarMonthTv.text = monthArray[YMList[1] - 1]
        }
    }

    /**
     * 클릭 리스너
     */
    private fun initClickListener() {
        /** 캘린더 페이지 */
        binding.calendarIncludeCalendarCalendar.apply {
            /** 등록하기 */
            calendarRegisterFrm.setOnClickListener {
                val spf = (requireContext()).getSharedPreferences("Date", MODE_PRIVATE)
                val editor = spf.edit()

                editor.putString("selectedDate", DateToStringHelper(selectedDate))
                editor.apply()

                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, ScheduleRegistrationFragment())
                    .commitAllowingStateLoss()
            }
            /** 날짜 선택 드롭다운 */
            calendarDropdownIv.setOnClickListener {
                showDateSelectDropdown(binding.calendarIncludeCalendarCalendar.calendarDropdownIv)
            }
            /** 날짜 선택 후 일정 소개 페이지로 이동 */
            calendarCalendar.setOnDateChangedListener { widget, date, selected ->

                calendarYearTv.text = String.format("${date.year}년")
                calendarMonthTv.text = String.format("${date.month}월")

                lifecycleScope.launch {
                    loadTasks(date.year, date.month)
                }

                // 날짜 별 일정 소개 페이지로 이동
                if (dateSelectState == "selected" && selectedDate == date) {
                    controlBottomNavigation(context as MainActivity, false)
                    controlTopBar(context as MainActivity, false)

                    binding.calendarIncludeCalendarCalendar.root.visibility = View.GONE
                    binding.calendarIncludeTaskOnDate.root.visibility = View.VISIBLE

                    val dayOfWeek = checkDayOfWeek(date.year, date.month, date.day)

                    binding.calendarIncludeTaskOnDate.taskOnDateDateTv.text = String.format("${date.year}년 ${date.month}월 ${date.day}일 (${dayOfWeek})")

                    // 날짜에 맞는 일정 갯수
                    initTaskOnDateRV(date)
                } else {
                    currentSelectionDecorator?.let {
                        calendarCalendar.removeDecorator(it)
                    }

                    val newSelectionDecorator = SelectionDecorator(date)
                    calendarCalendar.addDecorator(newSelectionDecorator)

                    currentSelectionDecorator = newSelectionDecorator

                    dateSelectState = "selected"
                    selectedDate = date
                }
            }
        }
        /** 일정 소개 페이지 */
        binding.calendarIncludeTaskOnDate.apply {
            /** 뒤로 가기 */
            taskOnDateBackIv.setOnClickListener {
                controlBottomNavigation(context as MainActivity, true)
                controlTopBar(context as MainActivity, true)

                binding.calendarIncludeTaskOnDate.root.visibility = View.GONE
                binding.calendarIncludeCalendarCalendar.root.visibility = View.VISIBLE
            }
        }
    }

    /**
     * 날짜 선택 드롭다운 출력
     */
    private fun showDateSelectDropdown(anchor: View) {
        selectedYearOnDropdown = null
        selectedMonthOnDropdown = null

        val inflater =  LayoutInflater.from(context as MainActivity)
        val popupView = inflater.inflate(R.layout.layout_calendar_dropdown, null)

        dropdownPopup = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        val yearForSelectList = popupView.findViewById<LinearLayout>(R.id.calendar_dropdown_yearList)
        val monthForSelectList = popupView.findViewById<LinearLayout>(R.id.calendar_dropdown_monthList)

        fun updateUI(list: LinearLayout, items: List<Int>, selected: Int?, onClick: (Int) -> Unit) {
            list.removeAllViews()
            for (item in items) {
                val isSelected = item == selected
                val textView = TextView(context as MainActivity).apply {
                    text = if(list == yearForSelectList) "${item}년" else "${item}월"
                    setPadding(24, 16, 24 ,16)
                    textSize = 12f
                    setTypeface(ResourcesCompat.getFont(context as MainActivity,
                        R.font.poppins_semibold
                    ))
                    setBackgroundColor(if (isSelected) "#F1F5F9".toColorInt() else Color.TRANSPARENT)
                    setOnClickListener {
                        onClick(item)
                    }
                }
                list.addView(textView)
            }
        }

        updateUI(yearForSelectList, yearsOnDropdown, selectedYearOnDropdown) { year ->
            selectedYearOnDropdown = year
            updateUI(yearForSelectList, yearsOnDropdown, selectedYearOnDropdown) {}
            checkCompleteSelection()
        }
        updateUI(monthForSelectList, monthsOnDropdown, selectedMonthOnDropdown) { month ->
            selectedMonthOnDropdown = month
            updateUI(monthForSelectList, monthsOnDropdown, selectedMonthOnDropdown) {}
            checkCompleteSelection()
        }

        dropdownPopup.apply {
            elevation = 8f
            setBackgroundDrawable(Color.WHITE.toDrawable())
            isOutsideTouchable = true
            showAsDropDown(anchor)
        }
    }

    /**
     * 연, 월 모두 선택되었는지 체크
     */
    private fun checkCompleteSelection() {
        if (selectedYearOnDropdown != null && selectedMonthOnDropdown != null) {
            binding.calendarIncludeCalendarCalendar.apply {
                calendarYearTv.text = String.format("${selectedYearOnDropdown}년")
                calendarMonthTv.text = String.format("${selectedMonthOnDropdown}월")
                calendarCalendar.setCurrentDate(CalendarDay.from(
                    selectedYearOnDropdown!!,
                    selectedMonthOnDropdown!!,
                    1))
                dropdownPopup.dismiss()
            }
        }
    }

    /**
     * 해당 날짜의 요일 확인
     */
    private fun checkDayOfWeek(year: Int, month: Int, day: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month-1, day)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

        return dayOfWeekList[dayOfWeek-1]
    }

    /**
     * TaskOnDateRVAdapter 초기화
     */
    private fun initTaskOnDateRVAdapter() {
        taskOnDateRVAdapter = TaskOnDateRVAdapter() {clickItem ->
            val spf = (requireContext()).getSharedPreferences("executedTask", MODE_PRIVATE)
            spf.edit() {
                putInt("taskId", clickItem.id)
            }

            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, ScheduleExecutionFragment())
                .commitAllowingStateLoss()

            controlTopBar(context as MainActivity, false)
            controlBottomNavigation(context as MainActivity, false)
        }
        binding.calendarIncludeTaskOnDate.taskOnDateTaskRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.calendarIncludeTaskOnDate.taskOnDateTaskRv.adapter = taskOnDateRVAdapter
    }

    /**
     * 해당 날짜별 일정 RV 초기화: 데이터 초기화
     */
    private fun initTaskOnDateRV(date: CalendarDay) {
        binding.calendarIncludeTaskOnDate.apply {

            val taskDate = String.format("${date.year}-${DecimalFormat("00").format(date.month)}-${DecimalFormat("00").format(date.day)}")

            taskOnDateList.clear()
            taskOnDateRVAdapter.deleteAllTasks()

            taskOnDateList.addAll(scheduleDB.taskDao().getTasksByDay(taskDate))
            taskOnDateRVAdapter.addTask(taskOnDateList)
            taskOnDateCountTv.text = String.format("일정 갯수 : ${taskOnDateList.size}개")
        }
    }

    /**
     * 미룬 일정 RV 초기화
     */
    private fun initDelayedRV() {
        binding.calendarIncludeCalendarCalendar.apply {
            calendarToDoRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

            val delayedRVAdapter = DelayedRVAdapter()
            calendarToDoRv.adapter = delayedRVAdapter
            delayedRVAdapter.addSchedule(scheduleDB.scheduleDao().getSchedules() as ArrayList<Schedule>)
        }
    }

    /**
     * 날짜별 Task 수에 따라 EventDecorator 적용
     */
    private fun loadDecorators() {
        Log.d("loadDecorator", "LoadDecorator")

        val decorators = mutableListOf<DayViewDecorator>()

        Log.d("흐름", "monthly.forEach 전")
        monthly.forEach { monthly ->
            Log.d("흐름", "monthly.forEach 후")

            Log.d("달", "date:${monthly.date}\nunfinishedCount:${monthly.unfinishedCount}")
            val ymd = monthly.date.split("-")

            Log.d("흐름", "decorators.add 전")
            decorators.add(
                EventDecorator(
                    day = CalendarDay.from(Integer.parseInt(ymd[0], 10), Integer.parseInt(ymd[1], 10), Integer.parseInt(ymd[2], 10)),
                    count = monthly.unfinishedCount,
                    countTextSize = 26f
                )
            )
        }
        Log.d("흐름", "monthly.forEach 끝")


        binding.calendarIncludeCalendarCalendar.calendarCalendar.apply {
            removeDecorators()
            addDecorators(decorators)
            invalidateDecorators()
        }

    }
}
