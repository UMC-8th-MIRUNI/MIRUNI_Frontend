package com.example.miruni

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ReportFragment
import com.example.miruni.databinding.FragmentCalendarBinding

class CalendarFragment : Fragment() {
    private lateinit var binding : FragmentCalendarBinding
    private var YMList = arrayOf(0, 0)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCalendarBinding.inflate(layoutInflater, container, false)

        initCalendar()
        initClickListener()

        return binding.root
    }

    private fun initCalendar() {
        val today = binding.calendarCalendar.currentDate
        YMList[0] = today.year.toInt()
        YMList[1] = today.month.toInt()

        Log.d("Calendar:today", "YMList = ${YMList[0]}년 ${YMList[1]}월")

        val monthArray = resources.getStringArray(R.array.monthArr)
        binding.apply {
            calendarYearTv.text = YMList[0].toString()
            calendarMonthTv.text = monthArray[YMList[1] - 1]
        }
    }

    private fun initClickListener() {
        val monthArray = resources.getStringArray(R.array.monthArr)

        binding.apply {
            calendarForwardIv.setOnClickListener {
                if (YMList[1] == 12) {
                    YMList[0]++
                    YMList[1] = 1
                    Log.d("Calendar", "다음 년도로 넘어감")
                } else {
                    YMList[1]++
                    Log.d("Calendar", "다음 달로 넘어감")
                }
                calendarYearTv.text = YMList[0].toString()
                calendarMonthTv.text = monthArray[YMList[1] - 1]
                Log.d("Calendar:Next", "YMList = ${YMList[0]}년 ${YMList[1]}월")
                calendarCalendar.goToNext()
            }
            calendarBackIv.setOnClickListener {
                if (YMList[1] == 1) {
                    YMList[0]--
                    YMList[1] = 12
                    Log.d("Calendar", "전 연도로 넘어감")
                } else {
                    YMList[1]--
                    Log.d("Calendar", "전 달로 넘어감")
                }
                calendarYearTv.text = YMList[0].toString()
                calendarMonthTv.text = monthArray[YMList[1] - 1]
                Log.d("Calendar:Previous", "YMList = ${YMList[0]}년 ${YMList[1]}월")
                calendarCalendar.goToPrevious()
            }

            /** 시작하기 */
            calendarStartBtn.setOnClickListener {
                val intent = Intent(requireContext(), ProcessingActivity::class.java)
                intent.putExtra("showFragment", "StartFragment")
                startActivity(intent)
            }

            /** 등록하기 */
            calendarCommitBtn.setOnClickListener {
                val intent = Intent(requireContext(), ProcessingActivity::class.java)
                intent.putExtra("showFragment", "CommitFragment")
                startActivity(intent)
            }
        }
    }
}