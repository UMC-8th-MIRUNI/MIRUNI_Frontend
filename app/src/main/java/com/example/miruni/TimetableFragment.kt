package com.example.miruni

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.databinding.LayoutTimetableBinding
import com.example.miruni.ui.calendar.ScheduleRegistrationFragment
import com.example.miruni.util.convertDateFormat
import com.example.miruni.util.getDateTimeStringHelper
import com.example.miruni.util.splitDateTimeHelper
import kotlinx.coroutines.launch

class TimetableFragment: Fragment() {
    val binding by lazy {
        LayoutTimetableBinding.inflate(layoutInflater)
    }
    private lateinit var db : ScheduleDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = ScheduleDatabase.getInstance(requireContext())!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("onViewCreated", "onViewCreated")

        // recyclerView 연결
        val dapter = TimetableRVAdapter(db)
        binding.timetableRV.adapter = dapter
        binding.timetableRV.layoutManager = LinearLayoutManager(requireContext())

        // 직전 Fragment 확인
        val backStackCount = parentFragmentManager.backStackEntryCount
        if (backStackCount > 0) {
            val prevEntry = parentFragmentManager.getBackStackEntryAt(backStackCount - 1)
            Log.d("onViewCreated", prevEntry.name.toString())
            if (prevEntry.name == "ScheduleRegistration") {
                val scheduleId = requireArguments().getInt("idForCheckSplit")
                val schedule = db.planDao().getPlan(scheduleId)

                loadScheduleData(scheduleId)
                binding.timetableTitle.text = schedule.title
                binding.timetableDeadline.text = convertDateFormat(splitDateTimeHelper(schedule.deadline!!, true), "-", ".")
                binding.timetableLevel.text = schedule.priority
                Log.d("Timetable", schedule.priority.toString())

                // 타임 테이블 설정
                dapter.addTasks(scheduleId!!)
            }
        }
    }

    private fun loadScheduleData(planId: Int) {
        lifecycleScope.launch {
            // /api/schedule/{planId}
        }
    }
}