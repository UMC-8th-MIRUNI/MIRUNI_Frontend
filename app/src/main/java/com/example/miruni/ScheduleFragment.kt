package com.example.miruni

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.miruni.databinding.FragmentScheduleBinding

class ScheduleFragment : Fragment() {

    private lateinit var binding : FragmentScheduleBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentScheduleBinding.inflate(layoutInflater, container, false)

        initSchedule()
        initClickListener()

        return binding.root
    }

    /**
     * DB에서
     */
    private fun initSchedule() {

    }

    private fun initClickListener() {

        /** 수정하기 */
        binding.scheduleEditBtn.setOnClickListener {

        }

        /** 등록하기 */
        binding.scheduleCommitBtn.setOnClickListener {
            val intent = Intent(activity, ProcessingActivity()::class.java)
            intent.putExtra("showFragment", "GrowFragment")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }
    }
}