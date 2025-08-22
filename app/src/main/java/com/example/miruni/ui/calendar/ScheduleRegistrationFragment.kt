package com.example.miruni.ui.calendar

import android.animation.ValueAnimator
import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.graphics.Rect
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import com.example.miruni.MainActivity
import com.example.miruni.R
import com.example.miruni.TimetableFragment
import com.example.miruni.TokenManager
import com.example.miruni.api.ApiService
import com.example.miruni.api.RegisterScheduleRequest
import com.example.miruni.api.SplitScheduleRequest
import com.example.miruni.api.getRetrofit
import com.example.miruni.data.Plan
import com.example.miruni.data.Priority
import com.example.miruni.data.Type
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.data.Task
import com.example.miruni.data.Time
import com.example.miruni.data.ampm
import com.example.miruni.databinding.FragmentScheduleRegistrationBinding
import com.example.miruni.databinding.LayoutDropdownPriorityBinding
import com.example.miruni.databinding.LayoutDropdownScheduleTypeBinding
import com.example.miruni.databinding.LayoutPopupRegisterScheduleSelectExecutionDateBinding
import com.example.miruni.databinding.LayoutPopupSplitDetailGuideBinding
import com.example.miruni.databinding.LayoutPopupSplitGuideBinding
import com.example.miruni.databinding.LayoutScheduleDelayAmpmBinding
import com.example.miruni.databinding.LayoutScheduleDelayCalendarBinding
import com.example.miruni.databinding.LayoutScheduleRegistrationTopbarBinding
import com.example.miruni.util.controlBottomNavigation
import com.example.miruni.util.controlTopBar
import com.example.miruni.util.splitDateTimeHelper
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.format.TitleFormatter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar

class ScheduleRegistrationFragment : Fragment() {
    private lateinit var binding: FragmentScheduleRegistrationBinding
    private lateinit var scheduleDB: ScheduleDatabase
    private lateinit var accessToken: String

    // 일정 등록하기
    private lateinit var priorityDropdown: PopupWindow
    private val hoursOnDropdown = (0..12).toList()
    private val minutesOnDropdown = (0..59).toList()
    private val ampmOnDropdown = listOf(ampm.AM, ampm.PM)
    private val priorityItems = arrayListOf("상", "중", "하")

    private var selectedDeadline: Time? = null // 마감 일시
    private var selectedExecutionStartDate: Time? = null // 시작 일시
    private var selectedExecutionEndDate: Time? = null // 종료 일시
    private var selectedPriority = "" // 우선 순위
    private var selectedScheduleType: Type = Type.IMMERSIVE // 일정 유형

    private var idForTimtable: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScheduleRegistrationBinding.inflate(layoutInflater, container, false)
        scheduleDB = ScheduleDatabase.getInstance(requireContext())!!
        accessToken = String.format("Bearer ${TokenManager.getToken(requireContext())}")

        controlBottomNavigation(context as MainActivity, false)
        controlTopBar(context as MainActivity, false)

        initDate()
        initRegistrationClickListener()
        initSplitClickListener()

        return binding.root
    }

    /**
     * 진입 날짜 초기화
     */
    private fun initDate() {
        val spf = (requireContext()).getSharedPreferences("Date", MODE_PRIVATE)
        val selectedDate = spf.getString("selectedDate", "").toString() // "yyyy-MM-dd"
        val tmpDate = selectedDate.split("-")

        val deadlineCalendar = Calendar.getInstance()
        deadlineCalendar.set(
            tmpDate[0].toInt(),
            tmpDate[1].toInt() - 1,
            tmpDate[2].toInt()
        )
        val startCalendar = Calendar.getInstance()
        startCalendar.set(
            tmpDate[0].toInt(),
            tmpDate[1].toInt() - 1,
            tmpDate[2].toInt()
        )
        val endCalendar = Calendar.getInstance()
        endCalendar.set(
            tmpDate[0].toInt(),
            tmpDate[1].toInt() - 1,
            tmpDate[2].toInt()
        )
        selectedDeadline = Time(
            deadlineCalendar,
            deadlineCalendar.get(Calendar.HOUR),
            deadlineCalendar.get(Calendar.MINUTE),
            if (deadlineCalendar.get(Calendar.HOUR_OF_DAY) > 11) ampm.PM else ampm.AM
        )
        selectedExecutionStartDate = Time(
            startCalendar,
            startCalendar.get(Calendar.HOUR),
            startCalendar.get(Calendar.MINUTE),
            if (startCalendar.get(Calendar.HOUR_OF_DAY) > 11) ampm.PM else ampm.AM
        )
        selectedExecutionEndDate = Time(
            endCalendar,
            endCalendar.get(Calendar.HOUR),
            endCalendar.get(Calendar.MINUTE),
            if (endCalendar.get(Calendar.HOUR_OF_DAY) > 11) ampm.PM else ampm.AM
        )
        endCalendar.add(Calendar.DAY_OF_MONTH, 1)

        binding.scheduleRegistrationInclude.scheduleRegistrationIncludeContent.scheduleRegistrationContentDeadlineTv.text =
            String.format("${selectedDeadline!!.date.get(Calendar.YEAR)}.${selectedDeadline!!.date.get(Calendar.MONTH) + 1}.${selectedDeadline!!.date.get(Calendar.DAY_OF_MONTH)}. ${if (selectedDeadline!!.ampm == ampm.AM) "오전" else "오후"} ${selectedDeadline!!.hour}:${selectedDeadline!!.minute}")
        binding.scheduleRegistrationInclude.scheduleRegistrationIncludeContent.scheduleRegistrationContentDateTv.text =
            String.format("${selectedExecutionStartDate!!.date.get(Calendar.YEAR)}.${selectedExecutionStartDate!!.date.get(Calendar.MONTH) + 1}.${selectedExecutionStartDate!!.date.get(Calendar.DAY_OF_MONTH)}. ${if (selectedExecutionStartDate!!.ampm == ampm.AM) "오전" else "오후"} ${selectedExecutionStartDate!!.hour}:${selectedExecutionStartDate!!.minute} - ${selectedExecutionEndDate!!.date.get(Calendar.YEAR)}.${selectedExecutionEndDate!!.date.get(Calendar.MONTH)}.${selectedExecutionEndDate!!.date.get(Calendar.DAY_OF_MONTH)}. ${if (selectedExecutionEndDate!!.ampm == ampm.AM) "오전" else "오후"} ${selectedExecutionEndDate!!.hour}:${selectedExecutionEndDate!!.minute}")
    }

    /**
     * '일정 등록하기' 화면 클릭 이벤트
     */
    private fun initRegistrationClickListener() {
        binding.scheduleRegistrationInclude.scheduleRegistrationIncludeTopbar.apply {
            /** 뒤로 가기 */
            scheduleRegistrationTopbarBackIv.setOnClickListener {
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, CalendarFragment())
                    .commitAllowingStateLoss()

                controlTopBar(context as MainActivity, true)
                controlBottomNavigation(context as MainActivity, true)
            }

            /** x 버튼 */
            scheduleRegistrationTopbarCancelIv.setOnClickListener {
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, CalendarFragment())
                    .commitAllowingStateLoss()

                controlTopBar(context as MainActivity, true)
                controlBottomNavigation(context as MainActivity, true)
            }
        }

        binding.scheduleRegistrationInclude.scheduleRegistrationIncludeContent.apply {
            /** 마감기한 설정 */
            scheduleRegistrationContentDeadlineFrm.setOnClickListener {
                showDeadlinePopup(it, selectedDeadline) {
                    selectedDeadline = it
                    scheduleRegistrationContentDeadlineTv.text =
                        String.format("${it.date.get(Calendar.YEAR)}.${it.date.get(Calendar.MONTH) + 1}.${it.date.get(Calendar.DAY_OF_MONTH)}. ${if (it.ampm == ampm.AM) "오전" else "오후"} ${it.hour}:${it.minute}")
                }
            }
            /** 일정 수행 날짜 설정 */
            scheduleRegistrationContentDateFrm.setOnClickListener {
                showExecutionDatePopup(
                    it,
                    selectedExecutionStartDate,
                    selectedExecutionEndDate,
                    resultStartTime = { resultStart -> selectedExecutionStartDate = resultStart},
                    resultEndTime = { resultEnd -> selectedExecutionEndDate = resultEnd}
                ) {
                    scheduleRegistrationContentDateTv.text =
                        String.format("${selectedExecutionStartDate!!.date.get(Calendar.YEAR)}.${selectedExecutionStartDate!!.date.get(Calendar.MONTH) + 1}.${selectedExecutionStartDate!!.date.get(Calendar.DAY_OF_MONTH)}. ${if (selectedExecutionStartDate!!.ampm == ampm.AM) "오전" else "오후"} ${selectedExecutionStartDate!!.hour}:${selectedExecutionStartDate!!.minute} - ${selectedExecutionEndDate!!.date.get(Calendar.YEAR)}.${selectedExecutionEndDate!!.date.get(Calendar.MONTH) + 1}.${selectedExecutionEndDate!!.date.get(Calendar.DAY_OF_MONTH)}. ${if (selectedExecutionEndDate!!.ampm == ampm.AM) "오전" else "오후"} ${selectedExecutionEndDate!!.hour}:${selectedExecutionEndDate!!.minute}")
                }
            }
            /** 우선 순위 설정 */
            scheduleRegistrationContentPriorityTv.setOnClickListener {
                showPriorityDropdown(scheduleRegistrationContentPriorityTv)
            }
        }

        binding.scheduleRegistrationInclude.scheduleRegistrationIncludeBtn.apply {
            /** 등록하기 */
            scheduleRegistrationContentBtnRegister.setOnClickListener {
                lifecycleScope.launch {
                    idForTimtable = registerSchedule()

                    (context as MainActivity).supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, CalendarFragment())
                        .commitAllowingStateLoss()

                    controlTopBar(context as MainActivity, true)
                    controlBottomNavigation(context as MainActivity, true)
                }
            }
            /** 쪼개기 */
            scheduleRegistrationContentBtnSplit.setOnClickListener {
                screenChange(
                    binding.scheduleRegistrationInclude.root,
                    binding.scheduleSplitInclude.root,
                    binding.scheduleRegistrationInclude.scheduleRegistrationIncludeTopbar,
                    "쪼개기",
                    true
                )
            }
        }
    }

    /**
     * '쪼개기' 화면 클릭 이벤트
     */
    private fun initSplitClickListener() {
        binding.scheduleSplitInclude.scheduleSplitIncludeTopbar.apply {
            /** 뒤로 가기 */
            scheduleRegistrationTopbarBackIv.setOnClickListener {
                binding.scheduleRegistrationInclude.root.visibility = View.VISIBLE
                binding.scheduleSplitInclude.root.visibility = View.GONE
                binding.scheduleSplitInclude.scheduleSplitIncludeTopbar.scheduleRegistrationTopbarTitleTv.text = "일정 등록하기"
            }
            /** x표시 */
            scheduleRegistrationTopbarCancelIv.setOnClickListener {
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, CalendarFragment())
                    .commitAllowingStateLoss()

                controlTopBar(context as MainActivity, true)
                controlBottomNavigation(context as MainActivity, true)
            }
        }
        binding.scheduleSplitInclude.apply {
            /** 작업 유형 선택 드롭다운 */
            scheduleSplitTypeFrm.setOnClickListener {
                scheduleSplitTypeMoreIv.visibility = View.GONE
                showScheduleTypeDropdown(it)
            }
            /** 쪼개기 가이드 드롭다운 */
            scheduleSplitSplitGuideIv.setOnClickListener {
                initSplitGuidePopup(it)
            }
            /** 쪼개기 */
            scheduleSplitSplitBtn.setOnClickListener {
                screenChange(
                    binding.scheduleSplitInclude.root,
                    binding.scheduleSplitLoadingInclude.root,
                    binding.scheduleSplitLoadingInclude.scheduleSplitLoadingIncludeTopbar,
                    null,
                    false
                )
                initSplitLoading()
                initSplitComplete()

                // 백엔드로 데이터 보내고 -> 화면 전환하고 -> 일정 시간 지나면(or 데이터 받으면) -> 완료 화면 띄우기
            }
        }
    }

    /**
     * 쪼개기 로딩 화면 설정
     */
    private fun initSplitLoading() {
        binding.scheduleSplitLoadingInclude.apply {

            /** 뒤로 가기 */
            scheduleSplitLoadingIncludeTopbar.scheduleRegistrationTopbarBackIv.setOnClickListener {
                screenChange(
                    binding.scheduleSplitLoadingInclude.root,
                    binding.scheduleSplitInclude.root,
                    binding.scheduleSplitInclude.scheduleSplitIncludeTopbar,
                    "쪼개기",
                    true
                )
            }

            lifecycleScope.launch {
                delay(1000)

                splitSchedule()

                screenChange(
                    binding.scheduleSplitLoadingInclude.root,
                    binding.scheduleSplitCompleteInclude.root,
                    binding.scheduleSplitCompleteInclude.scheduleSplitCompleteIncludeTopbar,
                    null,
                    false
                )

                animateSplitCompleteCheck()
            }
        }
    }

    /**
     * 쪼개기 완료 화면
     */
    private fun initSplitComplete() {
        binding.scheduleSplitCompleteInclude.apply {

            /** 뒤로 가기 */
            scheduleSplitCompleteIncludeTopbar.scheduleRegistrationTopbarBackIv.setOnClickListener {
                screenChange(
                    binding.scheduleSplitCompleteInclude.root,
                    binding.scheduleSplitInclude.root,
                    binding.scheduleSplitInclude.scheduleSplitIncludeTopbar,
                    "쪼개기",
                    true
                )
            }
            /** 확인 */
            scheduleSplitCompleteOkTv.setOnClickListener {

                val bundle = Bundle()
                bundle.putInt("idForCheckSplit", idForTimtable)
                Log.d("idForCheckSplit", idForTimtable.toString())

                val fragment = TimetableFragment().apply {
                    arguments = bundle
                }

                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, fragment, "TimetableFragment")
                    .addToBackStack("ScheduleRegistration")
                    .commit()
            }
        }
    }

    private fun animateSplitCompleteCheck() {

        binding.scheduleSplitCompleteInclude.apply {
            scheduleSplitCompleteCheckIv.viewTreeObserver.addOnGlobalLayoutListener ( object:
                ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    scheduleSplitCompleteCheckIv.viewTreeObserver.removeOnGlobalLayoutListener(this)

                    val checkImg = scheduleSplitCompleteCheckIv
                    Log.d("DEBUG", "width=${checkImg.width}, height=${checkImg.height}")

                    val fullWidth = checkImg.width
                    val fullHeight = checkImg.height

                    if (fullWidth == 0 || fullHeight == 0) {
                        Log.w("Animation", "이미지 크기가 0입니다. 애니메이션을 건너뜁니다.")
                        return
                    }
                    checkImg.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

                    checkImg.clipBounds = Rect(0, 0, 0, fullHeight)

                    val animator = ValueAnimator.ofInt(0, fullWidth).apply {
                        duration = 1000L
                        interpolator = DecelerateInterpolator()
                        addUpdateListener { anim ->
                            val currentWidth = anim.animatedValue as Int
                            checkImg.clipBounds = Rect(0, 0, currentWidth, checkImg.height)
                        }
                    }
                    animator.start()
                }
            })

        }
    }

    /**
     * 마감 날짜 선택
     */
    private fun showDeadlinePopup(
        anchor: View,
        selectedTimeBefore: Time?,
        onItemSelected: (Time) -> Unit) {

        val calendar = Calendar.getInstance()

        var selectedTime = Time(
            calendar,
            calendar.get(Calendar.HOUR),
            calendar.get(Calendar.MINUTE),
            ampm.AM
        )

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
        if (selectedTimeBefore != null) { selectedTime = selectedTimeBefore }
        dropdownView.scheduleDelayCalendarTimeHourTv.text = selectedTime.hour.toString()
        dropdownView.scheduleDelayCalendarTimeMinuteTv.text = selectedTime.minute.toString()
        dropdownView.scheduleDelayCalendarTimeAmpmTv.text = selectedTime.ampm.toString()
        Log.d("selectedTime", "${selectedTime.date} / ${selectedTime.ampm}")

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
                initTimeSelectDropdown(it, selectedTime, "hour") {
                    scheduleDelayCalendarTimeHourTv.text = it.toString()
                    selectedTime.hour = it
                }
            }
            scheduleDelayCalendarTimeMinute.setOnClickListener {
                initTimeSelectDropdown(it, selectedTime, "minute") {
                    scheduleDelayCalendarTimeMinuteTv.text = it.toString()
                    selectedTime.minute = it
                }
            }
            scheduleDelayCalendarTimeAmpm.setOnClickListener {
                initAmpmSelectDropdown(it) { idx ->
                    selectedTime.ampm = ampmOnDropdown[idx]
                    scheduleDelayCalendarTimeAmpmTv.text = if (selectedTime.ampm == ampm.AM) "오전" else "오후"
                }
            }
            scheduleDelayCalendarCalendar.setOnDateChangedListener { widget, date, selected ->
                selectedTime.date.set(
                    date.year,
                    date.month - 1,
                    date.day
                )
            }
            scheduleDelayCalendarOkTv.setOnClickListener {
                onItemSelected(selectedTime)
                calendarPopup.dismiss()
            }
        }

        calendarPopup.showAsDropDown(anchor)
    }

    /**
     * 실행 날짜 선택
     */
    private fun showExecutionDatePopup(
        anchor: View,
        selectedStartTimeBefore: Time?,
        selectedEndTimeBefore: Time?,
        resultStartTime: (Time) -> Unit,
        resultEndTime: (Time) -> Unit,
        onComplete:() -> Unit
    ) {
        val displayMetrics = requireContext().resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels

        val dropdownView = LayoutPopupRegisterScheduleSelectExecutionDateBinding.inflate(layoutInflater)
        // 달력 헤더
        dropdownView.scheduleRegistrationCalendar.setTitleFormatter(object : TitleFormatter {
            override fun format(day: CalendarDay?): CharSequence {
                return "${day!!.month}월 ${day.year}"
            }
        })

        // ui 초기화
        val startCalendar = Calendar.getInstance()
        val endCalendar = Calendar.getInstance()

        var selectedStartTime = Time(
            startCalendar,
            startCalendar.get(Calendar.HOUR),
            startCalendar.get(Calendar.MINUTE),
            ampm.AM
        )
        var selectedEndTime = Time(
            endCalendar,
            endCalendar.get(Calendar.HOUR),
            endCalendar.get(Calendar.MINUTE),
            ampm.AM
        )
        endCalendar.add(Calendar.MONTH, 1)

        if (selectedStartTimeBefore != null) { selectedStartTime = selectedStartTimeBefore }
        if (selectedEndTimeBefore != null) { selectedEndTime = selectedEndTimeBefore }

        dropdownView.scheduleRegistrationStartTimeHourTv.text = selectedStartTime.hour.toString()
        dropdownView.scheduleRegistrationStartTimeMinuteTv.text = selectedStartTime.minute.toString()
        dropdownView.scheduleRegistrationStartTimeAmpmTv.text = selectedStartTime.ampm.toString()

        dropdownView.scheduleRegistrationEndTimeHourTv.text = selectedEndTime.hour.toString()
        dropdownView.scheduleRegistrationEndTimeMinuteTv.text = selectedEndTime.minute.toString()
        dropdownView.scheduleRegistrationEndTimeAmpmTv.text = selectedEndTime.ampm.toString()

        val calendarPopup = PopupWindow(
            dropdownView.root,
            (screenWidth * 0.68).toInt(),
            (screenHeight * 0.46).toInt(),
            true
        )

        calendarPopup.elevation = 5f
        calendarPopup.isOutsideTouchable = true

        /** 각 시간대 선택 */
        dropdownView.apply {
            scheduleRegistrationStartTimeHour.setOnClickListener {
                initTimeSelectDropdown(it, selectedStartTime, "hour") {
                    scheduleRegistrationStartTimeHourTv.text = it.toString()
                    selectedStartTime.hour = it
                }
            }
            scheduleRegistrationStartTimeMinute.setOnClickListener {
                initTimeSelectDropdown(it, selectedStartTime, "minute") {
                    scheduleRegistrationStartTimeMinuteTv.text = it.toString()
                    selectedStartTime.minute = it
                }
            }
            scheduleRegistrationStartTimeAmpm.setOnClickListener {
                initAmpmSelectDropdown(it) { idx ->
                    selectedEndTime.ampm = ampmOnDropdown[idx]
                    scheduleRegistrationStartTimeAmpmTv.text = if (selectedEndTime.ampm == ampm.AM) "오전" else "오후"
                }
            }
            scheduleRegistrationEndTimeHour.setOnClickListener {
                initTimeSelectDropdown(it, selectedEndTime, "hour") {
                    scheduleRegistrationEndTimeHourTv.text = it.toString()
                    selectedEndTime.hour = it
                }
            }
            scheduleRegistrationEndTimeMinute.setOnClickListener {
                initTimeSelectDropdown(it, selectedEndTime, "minute") {
                    scheduleRegistrationEndTimeMinuteTv.text = it.toString()
                    selectedEndTime.minute = it
                }
            }
            scheduleRegistrationEndTimeAmpm.setOnClickListener {
                initAmpmSelectDropdown(it) { idx ->
                    selectedStartTime.ampm = ampmOnDropdown[idx]
                    scheduleRegistrationEndTimeAmpmTv.text = if (selectedStartTime.ampm == ampm.AM) "오전" else "오후"
                }
            }

            // 날짜 선택
            scheduleRegistrationCalendar.setOnDateChangedListener { widget, date, selected ->
                if (selected) {
                    selectedStartTime.date.set(date.year, date.month - 1, date.day)
                    selectedEndTime.date.set(date.year, date.month - 1, date.day)
                }
            }

            scheduleRegistrationCalendar.setOnRangeSelectedListener { widget, dates ->
                selectedStartTime.date.set(
                    dates[0].year,
                    dates[0].month - 1,
                    dates[0].day
                )
                selectedEndTime.date.set(
                    dates[dates.size - 1].year,
                    dates[dates.size - 1].month - 1,
                    dates[dates.size - 1].day
                )
            }
            scheduleRegistrationOkTv.setOnClickListener {
                resultStartTime(selectedStartTime)
                resultEndTime(selectedEndTime)
                onComplete()
                calendarPopup.dismiss()
            }
        }

        calendarPopup.showAsDropDown(anchor)
    }

    /**
     * 캘린더 팝업에서 시간 선택할 수 있는 드롭다운
     */
    private fun initTimeSelectDropdown(
        anchor: View,
        selectedTime: Time,
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
                    setPadding(24, 16, 24 ,16)
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
                updateUI(timeForSelectList, hoursOnDropdown, selectedTime.hour) { hour ->
                    selectedTime.hour = hour
                    onItemSelected(hour)
                    updateUI(timeForSelectList, hoursOnDropdown, selectedTime.hour) {}
                    dateSelectDropdown.dismiss()
                }
            }

            "minute" -> {
                updateUI(timeForSelectList, minutesOnDropdown, selectedTime.minute) { minute ->
                    selectedTime.minute = minute
                    onItemSelected(minute)
                    updateUI(timeForSelectList, minutesOnDropdown, selectedTime.minute) {}
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
    private fun initAmpmSelectDropdown(
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
     * 우선 순위 드롭다운 설정
     */
    private fun initPriorityDropdown(
        anchor: View,
        items: List<String>,
        selectedItem: String?,
        onItemSelected: (String) -> Unit) {

        anchor.setBackgroundResource(R.drawable.bg_selected_priority_dropdown)

        val dropdownView = LayoutDropdownPriorityBinding.inflate(layoutInflater)
        priorityDropdown = PopupWindow(
            dropdownView.root,
            anchor.width,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        priorityDropdown.elevation = 5f
        priorityDropdown.setBackgroundDrawable(Color.WHITE.toDrawable())
        priorityDropdown.isOutsideTouchable = true

        val priorityList = listOf(
            dropdownView.dropdownPriorityTop,
            dropdownView.dropdownPriorityMid,
            dropdownView.dropdownPriorityBtm
        )

        items.zip(priorityList).forEach { (text, textView) ->
            textView.text = text
            textView.setOnClickListener {
                onItemSelected(text)
                anchor.setBackgroundResource(R.drawable.bg_ababab_square_7)
                priorityDropdown.dismiss()
            }

            try{
                if (text == Priority.valueOf(selectedItem.toString()).localLabel) {
                    textView.setTextColor(Color.WHITE)
                    textView.setBackgroundColor("#1AE019".toColorInt())
                } else {
                    textView.setTextColor(Color.BLACK)
                    Color.TRANSPARENT
                }
            }catch (e: IllegalArgumentException) {
                null
            }
        }

        priorityDropdown.showAsDropDown(anchor, 0, -5)
    }

    /**
     * 쪼개기 가이드 팝업 설명
     */
    private fun initSplitGuidePopup(anchor: View) {
        val dropdownView = LayoutPopupSplitGuideBinding.inflate(layoutInflater)
        val splitGuidePopup = PopupWindow(
            dropdownView.root,
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            true
        )

        splitGuidePopup.elevation = 5f
        splitGuidePopup.isOutsideTouchable = true

        /** 상세 설명 보기 */
        dropdownView.apply {
            popupSplitGuideDetailTv.setOnClickListener {
                splitGuidePopup.dismiss()
                initSplitDetailGuidePopup(anchor)
            }
            popupSplitGuideCloseIv.setOnClickListener {
                splitGuidePopup.dismiss()
            }
        }
        splitGuidePopup.showAsDropDown(anchor, -10, -10)
    }

    /**
     * 쪼개기-일정 유형 선택 상세 설명 보기
     */
    private fun initSplitDetailGuidePopup(anchor: View) {
        val dropdownView = LayoutPopupSplitDetailGuideBinding.inflate(layoutInflater)
        val splitDetailGuidePopup = PopupWindow(
            dropdownView.root,
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            true
        )

        splitDetailGuidePopup.elevation = 5f
        splitDetailGuidePopup.isOutsideTouchable = true

        /** 상세 설명 보기 */
        dropdownView.apply {
            popupSplitDetailGuideCloseIv.setOnClickListener {
                splitDetailGuidePopup.dismiss()
            }
        }
        splitDetailGuidePopup.showAsDropDown(anchor, -10, -10)
    }

    /**
     * 일정 유형 선택 드롭다운 설정
     */
    private fun initScheduleTypeDropdown(
        anchor: View,
        onItemSelected: (Int) -> Unit) {

        anchor.setBackgroundResource(R.drawable.bg_selected_priority_dropdown)

        val dropdownView = LayoutDropdownScheduleTypeBinding.inflate(layoutInflater)
        val typeDropdown = PopupWindow(
            dropdownView.root,
            anchor.width,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        typeDropdown.elevation = 5f
        typeDropdown.setBackgroundDrawable(Color.WHITE.toDrawable())
        typeDropdown.isOutsideTouchable = true

        val typeViewList = listOf(
            dropdownView.dropdownScheduleType1,
            dropdownView.dropdownScheduleType2,
            dropdownView.dropdownScheduleType3,
            dropdownView.dropdownScheduleType4,
            dropdownView.dropdownScheduleType5,
            dropdownView.dropdownScheduleType6,
            dropdownView.dropdownScheduleType7
        )

        val viewList = listOf(
            Triple(dropdownView.dropdownScheduleType1Rank, dropdownView.dropdownScheduleType1Iv, dropdownView.dropdownScheduleType1Tv),
            Triple(dropdownView.dropdownScheduleType2Rank, dropdownView.dropdownScheduleType2Iv, dropdownView.dropdownScheduleType2Tv),
            Triple(dropdownView.dropdownScheduleType3Rank, dropdownView.dropdownScheduleType3Iv, dropdownView.dropdownScheduleType3Tv),
            Triple(dropdownView.dropdownScheduleType4Rank, dropdownView.dropdownScheduleType4Iv, dropdownView.dropdownScheduleType4Tv),
            Triple(dropdownView.dropdownScheduleType5Rank, dropdownView.dropdownScheduleType5Iv, dropdownView.dropdownScheduleType5Tv),
            Triple(dropdownView.dropdownScheduleType6Rank, dropdownView.dropdownScheduleType6Iv, dropdownView.dropdownScheduleType6Tv),
            Triple(dropdownView.dropdownScheduleType7Rank, dropdownView.dropdownScheduleType7Iv, dropdownView.dropdownScheduleType7Tv),
        )

        typeViewList.forEachIndexed() { index, type  ->
            type.setOnClickListener {
                onItemSelected(index)
                typeDropdown.dismiss()
                anchor.setBackgroundResource(R.drawable.bg_ababab_square_7)
            }

            if (index == selectedScheduleType.ordinal) {
                viewList[index].first.setTextColor(Color.WHITE)
                viewList[index].second.setColorFilter(Color.WHITE)
                viewList[index].first.setTextColor(Color.WHITE)

                type.setBackgroundColor("#1AE019".toColorInt())
            } else {
                viewList[index].first.setTextColor("#666666".toColorInt())
                viewList[index].second.setColorFilter("#666666".toColorInt())
                viewList[index].first.setTextColor("#666666".toColorInt())
                Color.TRANSPARENT
            }
        }

        typeDropdown.showAsDropDown(anchor, 0, -5)

    }


    /**
     * 우선 순위 드롭다운 결과 처리
     */
    private fun showPriorityDropdown(anchor: View) {
        initPriorityDropdown(anchor, priorityItems, selectedPriority) { selected ->
            selectedPriority = Priority.fromLocalLabel(selected).toString()
            Log.d("scheduleRegistration", "showPriorityDropdown: $selectedPriority")
            binding.scheduleRegistrationInclude.scheduleRegistrationIncludeContent.scheduleRegistrationContentPriorityTv.text = selected
        }
    }

    /**
     * 일정 유형 선택 드롭다운 결과 처리
     */
    private fun showScheduleTypeDropdown(anchor: View) {
        initScheduleTypeDropdown(anchor) {selected ->
            selectedScheduleType = Type.values()[selected]
            binding.scheduleSplitInclude.scheduleSplitTypeTv.text = Type.valueOf(selectedScheduleType.toString()).localLabel
            Log.d("scheduleRegistration", "split type: ${Type.values()[selected]}")
            binding.scheduleSplitInclude.scheduleSplitTypeMoreIv.visibility = View.VISIBLE
        }
    }

    /**
     * 백엔드로 보낼 Request 바디 생성
     */
    private fun setRegisterScheduleRequest(): RegisterScheduleRequest? {

        val numberFormat = DecimalFormat("00")

        val title = binding.scheduleRegistrationInclude.scheduleRegistrationIncludeContent.scheduleRegistrationContentTitleEt.text.toString()
        Log.d("RegistrationSchedule/title", title)

        val deadline =
            String.format("${numberFormat.format(selectedDeadline?.date?.get(Calendar.YEAR))}" +
                    "-${numberFormat.format(selectedDeadline?.date?.get(Calendar.MONTH)!! + 1)}" +
                    "-${numberFormat.format(selectedDeadline?.date?.get(Calendar.DAY_OF_MONTH))}" +
                    "T${numberFormat.format(selectedDeadline?.hour?.plus(if (selectedDeadline?.ampm == ampm.AM) 0 else 12))}" +
                    ":${numberFormat.format(selectedDeadline?.minute)}:00.000")
        Log.d("RegistrationSchedule/deadline", deadline)

        val scheduledStart =
            String.format("${numberFormat.format(selectedExecutionStartDate?.date?.get(Calendar.YEAR))}" +
                    "-${numberFormat.format(selectedExecutionStartDate?.date?.get(Calendar.MONTH)!! + 1)}" +
                    "-${numberFormat.format(selectedExecutionStartDate?.date?.get(Calendar.DAY_OF_MONTH))}" +
                    "T${numberFormat.format(selectedExecutionStartDate?.hour?.plus(if (selectedExecutionStartDate?.ampm == ampm.AM) 0 else 12))}" +
                    ":${numberFormat.format(selectedExecutionStartDate?.minute)}:00.000")
        Log.d("RegistrationSchedule/scheduledStart", scheduledStart)


        val scheduledEnd =
            String.format("${numberFormat.format(selectedExecutionEndDate?.date?.get(Calendar.YEAR))}" +
                    "-${numberFormat.format(selectedExecutionEndDate?.date?.get(Calendar.MONTH)!! + 1)}" +
                    "-${numberFormat.format(selectedExecutionEndDate?.date?.get(Calendar.DAY_OF_MONTH))}" +
                    "T${numberFormat.format(selectedExecutionEndDate?.hour?.plus(if (selectedExecutionEndDate?.ampm == ampm.AM) 0 else 12))}" +
                    ":${numberFormat.format(selectedExecutionEndDate?.minute)}:00.000")
        Log.d("RegistrationSchedule/scheduledEnd", scheduledEnd)

        val priority = selectedPriority
        Log.d("RegistrationSchedule/priority", priority.toString())

        val description = binding.scheduleRegistrationInclude.scheduleRegistrationIncludeContent.scheduleRegistrationContentCommentEt.text.toString()
        Log.d("RegistrationSchedule/description", description)

        if (title.isEmpty()) {
            Toast.makeText(context as MainActivity, "제목을 입력해주세요", Toast.LENGTH_SHORT).show()
            return null
        }
        if (deadline.isEmpty()) {
            Toast.makeText(context as MainActivity, "마감기한 혹은 수행 날짜를 정해주세요", Toast.LENGTH_SHORT).show()
            return null
        }
        if (priority == null) {
            Toast.makeText(context as MainActivity, "우선 순위를 정해주세요", Toast.LENGTH_SHORT).show()
            return null
        }
        if (description.isEmpty()) {
            Toast.makeText(context as MainActivity, "한 줄 설명은 작성하지 않았어요", Toast.LENGTH_SHORT).show()
        }

        return RegisterScheduleRequest(
            title,
            deadline,
            scheduledStart,
            scheduledEnd,
            priority,
            description
        )
    }

    /**
     * 일정 등록
     */
    private suspend fun registerSchedule(): Int {
        var planId = -1

        try {
            val registerScheduleRequest = setRegisterScheduleRequest() ?: return planId
            Log.d("registerSchedule/Request", "title: ${registerScheduleRequest.title} \ndeadline: ${registerScheduleRequest.deadline} \nscheduledStart: ${registerScheduleRequest.scheduledStart} \nscheduledEnd: ${registerScheduleRequest.scheduledEnd} \npriority: ${registerScheduleRequest.priority} \ndescription: ${registerScheduleRequest.description}")

            val api = getRetrofit().create(ApiService::class.java)
            val response = api.registerSchedule(accessToken, registerScheduleRequest)

            if (response.isSuccessful) {
                val resultOfResponse = response.body()!!.result

                val plan = Plan(
                    id = resultOfResponse.planId,
                    title = resultOfResponse.title,
                    deadline = resultOfResponse.deadline,
                    scheduledStart = resultOfResponse.scheduledStart,
                    isDone = resultOfResponse.isDone,
                )

                val task = Task(
                    id = resultOfResponse.planId,
                    title = resultOfResponse.title,
                    executeDay = splitDateTimeHelper(registerScheduleRequest.scheduledStart, true),
                    startTime = splitDateTimeHelper(registerScheduleRequest.scheduledStart, false),
                    endTime = splitDateTimeHelper(registerScheduleRequest.scheduledEnd, false),
                    status = "NOT_STARTED"
                )

                scheduleDB.taskDao().insert(task)
                Log.d("registerSchedule", "title: ${resultOfResponse.title}")
                scheduleDB.planDao().insert(plan)
                planId = resultOfResponse.planId
                Log.d("registerSchedule", planId.toString())
                Log.d("registerSchedule", "저장된 정보: ${response.body()}")
            } else {
                Log.d("registerSchedule", "실패: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Log.e("registerSchedule", "에러: ${e.message}")
        }

        idForTimtable = planId
        return planId
    }

    /**
     * 레이아웃 전환
     */
    private fun screenChange(
        originView: View,
        targetView: View,
        topbar: LayoutScheduleRegistrationTopbarBinding,
        topbarText: String?,
        closeVisibility: Boolean) {

        originView.visibility = View.GONE
        targetView.visibility = View.VISIBLE

        if (topbarText.isNullOrEmpty()) topbar.scheduleRegistrationTopbarTitleTv.visibility = View.GONE
        if (!closeVisibility) {
            topbar.scheduleRegistrationTopbarCancelIv.visibility = View.GONE
        }
    }

    private suspend fun splitSchedule() {
        val taskRange: String
        val detailRequest: String

        binding.scheduleSplitInclude.apply {
            taskRange = scheduleSplitVolumeEt.text.toString()
            detailRequest = scheduleSplitRequestEt.text.toString()
        }

        val request = SplitScheduleRequest(
            planType = selectedScheduleType.toString(),
            taskRange = taskRange,
            detailRequest = detailRequest
        )
        Log.d("splitSchedule",
            "planType: ${request.planType}" +
                    "\ntaskRange: ${request.taskRange}" +
                    "\ndetailRequest: ${request.detailRequest}")

        val planId = registerSchedule()
        scheduleDB.taskDao().deleteTaskById(planId)

        try {
            if (planId != -1) { // 일정이 정상적으로 등록됨
                val api = getRetrofit().create(ApiService::class.java)
                val response = api.splitSchedule(accessToken, planId, request)

                if (response.isSuccessful) {
                    val resultOfGetSplitSchedule = api.getSchedule(accessToken, planId).body()!!.result

                    val plan = Plan(
                        id = planId,
                        title = resultOfGetSplitSchedule.title,
                        planType = selectedScheduleType.toString(),
                        category = resultOfGetSplitSchedule.category,
                        deadline = resultOfGetSplitSchedule.deadline,
                        taskRange = resultOfGetSplitSchedule.taskRange,
                        priority = resultOfGetSplitSchedule.priority
                    )
                    scheduleDB.planDao().update(plan)

                    resultOfGetSplitSchedule.plans.forEach { splitSchedule ->
                        val task = Task(
                            id = splitSchedule.planId,
                            scheduleId = planId,
                            title = splitSchedule.description,
                            executeDay = splitSchedule.date,
                            startTime = splitSchedule.startTime,
                            endTime = splitSchedule.endTime,
                            status = "NOT_STARTED"
                        )
                        scheduleDB.taskDao().insert(task)
                        Log.d("splitSchedule", "일정 쪼개기 성공")
                    }
                } else if (response.code() == 500) {
                    Toast.makeText(requireContext(), "AI 응답 없음", Toast.LENGTH_SHORT).show()
                } else {
                    Log.d("splitSchedule", "일정 쪼개기 실패: ${response.code()} / ${response.body()?.message}")
                }
            } else { // 일정이 정상적으로 등록되지 않아 기본값 -1이 반환됨
                Log.e("splitSchedule", "등록 단계에서 오류")
                throw Exception("정상적으로 등록된 일정이 아닙니다.")
            }
        } catch (e: Exception) {
            Log.e("splitSchedule", "에러: ${e.message}")
        }
    }
}