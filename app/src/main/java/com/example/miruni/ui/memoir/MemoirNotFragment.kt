package com.example.miruni.ui.memoir

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.example.miruni.R
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.databinding.FragmentMemoirNotBinding

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

        val planId = arguments?.getInt("planId") ?: -1
        val aiPlanId = arguments?.getInt("aiPlanId") ?: -1

        db = ScheduleDatabase.getInstance(requireContext())!!

        /*val item = db.planDao().getPlan(aiPlanId)
        binding.notMemoirTitle.memoirWriteTitle.text = item.title
        binding.notMemoirTitle.memoirWriteDate.text = item.scheduledStart
        binding.notMemoirTitle.memoirDescription.text = item.description*/

        /* 회고 작성 페이지로 이동 */
        binding.moveWrite.setOnClickListener {
            val fragment = MemoirWriteFragment()
            val bundle = Bundle()
            bundle.putInt("planId", planId)
            bundle.putInt("aiPanId", aiPlanId)
            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.main_frm, fragment)
                commit()
            }
        }

    }
}