package com.example.miruni

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
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

class CalendarFragment : Fragment() {
    private lateinit var binding : FragmentCalendarBinding
    private var YMList = arrayOf(0, 0)
    private var pinnedList = ArrayList<Schedule>()

    private lateinit var dropdownPopup: PopupWindow
    private var selectedYearOnDropdown: Int? = null
    private var selectedMonthOnDropdown: Int? = null
    private val yearsOnDropdown = (2020..2040).toList()
    private val monthsOnDropdown = (1..12).toList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCalendarBinding.inflate(layoutInflater, container, false)

        // 더미 데이터
        initDummyData()

        initPinned()
        initCalendar()
        initDecorator()
        initClickListener()

        return binding.root
    }

    private fun initDummyData() {
        pinnedList.add(
            Schedule(
                "토익 LC 공부하기",
                " ",
                "2025.07.04"
            )
        )
        pinnedList.add(
            Schedule(
                "토익 RC 공부하기",
                " ",
                "2025.07.04"
            )
        )
    }

    private fun initPinned() {
        binding.calendarToDoRv.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val pinnedRVAdapter = PinnedRVAdapter()
        binding.calendarToDoRv.adapter = pinnedRVAdapter

        pinnedRVAdapter.addSchedule(pinnedList)
    }

    private fun initCalendar() {
        val today = binding.calendarCalendar.currentDate
        YMList[0] = today.year.toInt()
        YMList[1] = today.month.toInt()

        Log.d("Calendar:today", "YMList = ${YMList[0]}년 ${YMList[1]}월")

        val monthArray = resources.getStringArray(R.array.monthArr)
        binding.apply {
            calendarYearTv.text = String.format(YMList[0].toString()+"년")
            calendarMonthTv.text = monthArray[YMList[1] - 1]
        }

        binding.calendarDropdownIv.setOnClickListener {
            showDateSelectDropdown(binding.calendarDropdownIv)
        }
    }

    private fun initDecorator() {
        val selectedDecorator = selectDecorator(requireContext(),null)
        binding.calendarCalendar.addDecorator(selectedDecorator)

        binding.calendarCalendar.setOnDateChangedListener { widget, date, selected ->
            selectedDecorator.setDate(date)
            binding.calendarCalendar.invalidateDecorators()
        }
    }

    private fun initClickListener() {
        val monthArray = resources.getStringArray(R.array.monthArr)
    }

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
                    setTypeface(ResourcesCompat.getFont(context as MainActivity, R.font.poppins_semibold))
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

        dropdownPopup.elevation = 8f
        dropdownPopup.setBackgroundDrawable(ColorDrawable(Color.WHITE))
        dropdownPopup.isOutsideTouchable = true
        dropdownPopup.showAsDropDown(anchor)
    }

    private fun checkCompleteSelection() {
        if (selectedYearOnDropdown != null && selectedMonthOnDropdown != null) {
            binding.calendarYearTv.text = String.format("${selectedYearOnDropdown}년")
            binding.calendarMonthTv.text = String.format("${selectedMonthOnDropdown}월")
            binding.calendarCalendar.setCurrentDate(CalendarDay.from(
                selectedYearOnDropdown!!,
                selectedMonthOnDropdown!!,
                1))
            dropdownPopup.dismiss()
        }
    }
}