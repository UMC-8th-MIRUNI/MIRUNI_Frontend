package com.example.miruni

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miruni.api.ApiService
import com.example.miruni.api.SplitSchedule
import com.example.miruni.api.getRetrofit
import com.example.miruni.data.Plan
import com.example.miruni.data.Priority
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.data.Task
import com.example.miruni.data.repository.HomepageRepository
import com.example.miruni.databinding.LayoutTimetableBinding
import com.example.miruni.ui.calendar.CalendarFragment
import com.example.miruni.ui.calendar.ScheduleRegistrationFragment
import com.example.miruni.ui.homepage.HTimetableRVAdapter
import com.example.miruni.ui.homepage.HomepageFragment
import com.example.miruni.ui.homepage.HomepageViewModel
import com.example.miruni.ui.homepage.HomepageViewModelFactory
import com.example.miruni.util.controlBottomNavigation
import com.example.miruni.util.controlTopBar
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

        initClickListener()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity?.findViewById<View>(R.id.main_nav))?.visibility = View.GONE
        (activity?.findViewById<View>(R.id.main_top_bar))?.visibility = View.GONE
        Log.d("onViewCreated", "onViewCreated")

        // recyclerView 연결
        val adapter = TimetableRVAdapter(db)
        binding.timetableRV.adapter = adapter
        binding.timetableRV.layoutManager = LinearLayoutManager(requireContext())

        /* 홈페이지로부터 옴 */
        if(arguments?.getString("fromHomepageFragment") == "HomepageFragment"){

            binding.timetableOkTv.visibility = View.GONE
            binding.timetableResplitTv.visibility = View.GONE

            val homePageAdapter = HTimetableRVAdapter()
            binding.timetableRV.adapter = homePageAdapter
            binding.timetableRV.layoutManager = LinearLayoutManager(requireContext())

            val repository = HomepageRepository()
            val factory = HomepageViewModelFactory(repository)
            val viewModel = ViewModelProvider(this, factory)[HomepageViewModel::class.java]

            val token = String.format("Bearer ${TokenManager.getToken(requireContext())}")
            val planId = arguments?.getInt("planId") ?: 0
            val aiPlanId = arguments?.getInt("aiPlanId") ?: 0
            viewModel.getSchedule(token, planId)

            viewModel.scheduleData.observe(viewLifecycleOwner) { data ->
                binding.timetableTitle.text = data.title
                val deadLine = data.deadline.split("T")
                binding.timetableDeadline.text = deadLine[0]
                binding.timetableTaskRange.text = data.taskRange
                binding.timetableLevel.text = data.priority

                homePageAdapter.updateData(data.plans, aiPlanId)
            }
            binding.timetableBackIv.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStack()
            }
        }
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
                adapter.addTasks(scheduleId!!)
            }
        }
    }

    /**
     * 클릭 이벤트
     */
    private fun initClickListener() {
        binding.timetableOkTv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, CalendarFragment())
                .commitAllowingStateLoss()

            controlTopBar(context as MainActivity, true)
            controlBottomNavigation(context as MainActivity, true)
        }
        binding.timetableResplitTv.setOnClickListener {

        }
        binding.timetableBackIv.setOnClickListener {
            (context as MainActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, ScheduleRegistrationFragment())
                .commitAllowingStateLoss()

            controlTopBar(context as MainActivity, true)
            controlBottomNavigation(context as MainActivity, true)
        }
    }

    /**
     * 일정 유형 맞춰서 아이콘 매칭
     */
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