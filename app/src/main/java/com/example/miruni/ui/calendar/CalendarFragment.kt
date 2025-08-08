package com.example.miruni.ui.calendar

import android.graphics.Color
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
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
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import java.util.Calendar

class CalendarFragment : Fragment() {
    /** 전역 변수 */
    // 뷰 바인딩
    private lateinit var binding : FragmentCalendarBinding

    // 데이터 관리
    private lateinit var scheduleDB: ScheduleDatabase
    private var taskOnDateList = ArrayList<Task>()
    private lateinit var taskOnDateRVAdapter: TaskOnDateRVAdapter

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

        /** 바텀 네비게이션 설정 */
        initBottomNavigation()

        /** 데이터 관리 */
//        loadTasks()

        /** 캘린더 관련 설정 */
        initCalendar()
        initTaskOnDateRVAdapter()
        initClickListener()
        initDelayedRV()
        loadDecorators()

        return binding.root
    }

    /**
     * Bottom Navigation 초기화
     */
    private fun initBottomNavigation() {
        val activity = requireActivity() as MainActivity
        val navigationBar = activity.findViewById<ConstraintLayout>(R.id.main_nav)
        val homeBtn = activity.findViewById<ImageView>(R.id.nav_home_iv)

        navigationBar.visibility = View.VISIBLE
        homeBtn.visibility = View.VISIBLE
    }

    /**
     * 서버에서 Task 로드
     */
    private fun loadTasks() {
        TODO()
    }

    /**
     * 달력 초기화
     */
    private fun initCalendar() {
        val today = binding.calendarIncludeCalendarCalendar.calendarCalendar.currentDate
        YMList[0] = today.year.toInt()
        YMList[1] = today.month.toInt()

        Log.d("Calendar:today", "YMList = ${YMList[0]}년 ${YMList[1]}월")

        val monthArray = resources.getStringArray(R.array.monthArr)
        binding.calendarIncludeCalendarCalendar.apply {
            calendarYearTv.text = String.format(YMList[0].toString()+"년")
            calendarMonthTv.text = monthArray[YMList[1] - 1]
        }
    }

    /**
     * TaskOnDateRVAdapter 초기화
     */
    private fun initTaskOnDateRVAdapter() {
        taskOnDateRVAdapter = TaskOnDateRVAdapter()
        binding.calendarIncludeTaskOnDate.taskOnDateTaskRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.calendarIncludeTaskOnDate.taskOnDateTaskRv.adapter = taskOnDateRVAdapter
    }

    /**
     * 클릭 리스너
     */
    private fun initClickListener() {
        /** 캘린더 페이지 */
        binding.calendarIncludeCalendarCalendar.apply {
            /** 등록하기 */
            calendarRegisterFrm.setOnClickListener {
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, RegistrationScheduleFragment())
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

                // 날짜 별 일정 소개 페이지로 이동
                if (dateSelectState == "selected" && selectedDate == date) {
                    val activity = requireActivity() as MainActivity
                    val navigationBar = activity.findViewById<ConstraintLayout>(R.id.main_nav)
                    val homeBtn = activity.findViewById<ImageView>(R.id.nav_home_iv)

                    navigationBar.visibility = View.GONE
                    homeBtn.visibility = View.GONE
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
                initBottomNavigation()
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
        val popupView = inflater.inflate(R.layout.layout_drop_down_menu, null)

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
        val taskList = scheduleDB.taskDao().getTasks()

        val decorators = mutableListOf<DayViewDecorator>()

        taskList.forEach { task ->
            val ymd = task.executeDay.split("-")

            decorators.add(
                EventDecorator(
                    day = CalendarDay.from(ymd[0].toInt(), ymd[1].toInt(), ymd[2].toInt()),
                    count = scheduleDB.taskDao().getTasksByDay(task.executeDay).size,
                    countTextSize = 26f
                )
            )
        }

        binding.calendarIncludeCalendarCalendar.calendarCalendar.apply {
            removeDecorators()
            addDecorators(decorators)
            invalidateDecorators()
        }
    }
}