package com.example.miruni.ui.memoir

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import androidx.lifecycle.ViewModelProvider
import com.example.miruni.R
import com.example.miruni.TokenManager
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.data.repository.HomepageRepository
import com.example.miruni.databinding.FragmentMemoirNotBinding
import com.example.miruni.ui.homepage.HomepageViewModel
import com.example.miruni.ui.homepage.HomepageViewModelFactory

class MemoirNotFragment: Fragment() {
    val binding by lazy {
        FragmentMemoirNotBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }
    private lateinit var db: ScheduleDatabase
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity?.findViewById<View>(R.id.main_top_bar))?.visibility = View.GONE
        (activity?.findViewById<View>(R.id.main_nav))?.visibility = View.GONE

        val planId = requireArguments().getInt("planId")
        val aiPlanId = arguments?.getInt("aiPlanId") ?: -1

        db = ScheduleDatabase.getInstance(requireContext())!!
        val item = db.planDao().getPlan(aiPlanId)

        val repository = HomepageRepository()
        val factory = HomepageViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[HomepageViewModel::class.java]
        val token = String.format("Bearer ${TokenManager.getToken(requireContext())}")

        viewModel.getSchedule(token, planId)
        viewModel.scheduleData.observe(viewLifecycleOwner) { data ->
            for(i in data.plans){
                if(aiPlanId == i.planId){
                    binding.notMemoirTitle.memoirWriteTitle.text = i.description
                    val startTime = i.startTime.split(":")
                    binding.notMemoirTitle.memoirWriteDate.text = "${i.date} ${startTime[0]}:${startTime[1]}"
                }
            }
        }

        /* 회고 작성 페이지로 이동 */
        binding.moveWrite.setOnClickListener {
            val fragment = MemoirWriteFragment()
            val bundle = Bundle()
            bundle.putInt("planId", planId)
            bundle.putInt("aiPlanId", aiPlanId)
            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.main_frm, fragment)
                commit()
            }
        }

    }
}