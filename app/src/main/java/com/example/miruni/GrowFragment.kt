package com.example.miruni

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.miruni.databinding.FragmentGrowBinding

class GrowFragment : Fragment() {

    private lateinit var binding: FragmentGrowBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGrowBinding.inflate(layoutInflater, container, false)

        initClickListener()

        return binding.root
    }

    /**
     * 이벤트 처리
     */
    private fun initClickListener() {
        binding.growStopBtn.setOnClickListener {

        }

        binding.growCompleteBtn.setOnClickListener {
            (context as ProcessingActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.process_frm, CompleteFragment())
                .commitAllowingStateLoss()
        }
    }
}