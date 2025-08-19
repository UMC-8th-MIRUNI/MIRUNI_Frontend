package com.example.miruni

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miruni.api.ApiService
import com.example.miruni.api.getRetrofit
import com.example.miruni.data.Plan
import com.example.miruni.data.Priority
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.data.Task
import com.example.miruni.databinding.LayoutTimetableBinding
import com.example.miruni.util.convertDateFormat
import com.example.miruni.util.splitDateTimeHelper
import kotlinx.coroutines.launch

class TimetableFragment: Fragment() {
    val binding by lazy {
        LayoutTimetableBinding.inflate(layoutInflater)
    }
    private lateinit var db : ScheduleDatabase
    private lateinit var accessToken: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        db = ScheduleDatabase.getInstance(requireContext())!!
        accessToken = TokenManager.getToken(requireContext())!!

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
                Log.d("Timetable", "scheduleId: $scheduleId")
                val schedule = db.planDao().getPlan(scheduleId)

                matchTypeToIcon(schedule)
                binding.timetableTitle.text = schedule.title
                Log.d("Timetable", "title: ${schedule.title.toString()}")
                // 한 줄 소개도 저장해야 될 것 같음.
                binding.timetableDeadline.text = convertDateFormat(splitDateTimeHelper(schedule.deadline!!, true), "-", ".")
                binding.timetableTaskRange.text = schedule.taskRange
                Log.d("Timetable", schedule.priority.toString())
                binding.timetableLevel.text = Priority.valueOf(schedule.priority.toString()).localLabel

                // 타임 테이블 설정
                dapter.addTasks(scheduleId!!)
            }
        }
    }

    private fun matchTypeToIcon(schedule: Plan) {
        val type = schedule.planType
        Log.d("Timetable", "matchTypeToIcon-type: ${type}")

        when (type) {
            "IMMERSIVE" -> binding.timetableTypeIv.setImageResource(R.drawable.ic_brain)
            "CREATIVE" -> binding.timetableTypeIv.setImageResource(R.drawable.ic_brush)
            "STUDY_ORGANIZATION" -> binding.timetableTypeIv.setImageResource(R.drawable.ic_book)
            "PRACTICAL_ADMIN" -> binding.timetableTypeIv.setImageResource(R.drawable.ic_document)
            "ROUTINE" -> binding.timetableTypeIv.setImageResource(R.drawable.ic_routine)
            "COLLAB_COMMUNICATION" -> binding.timetableTypeIv.setImageResource(R.drawable.ic_bag)
            "PREPARATION_PLANNING" -> binding.timetableTypeIv.setImageResource(R.drawable.ic_plan)
        }
    }
}