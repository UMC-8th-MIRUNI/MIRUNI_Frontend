package com.example.miruni

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ReportFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miruni.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {
    private lateinit var binding : FragmentCalendarBinding
    private var YMList = arrayOf(0, 0)
    private var pinnedList = ArrayList<Schedule>()

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

        binding.apply {
            calendarDownIv.setOnClickListener {
                Toast.makeText(context as MainActivity, "날짜 선택 드롭다운", Toast.LENGTH_SHORT).show()
            }
        }
    }
}