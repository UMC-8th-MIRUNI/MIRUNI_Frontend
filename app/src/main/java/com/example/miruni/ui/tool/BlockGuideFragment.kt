package com.example.miruni.ui.tool

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.miruni.R
import com.example.miruni.databinding.FragmentBlockGuideBinding
import com.example.miruni.ui.calendar.ScheduleExecutionFragment

/**
 *
 * 홈페이지 에서 오는 방해요소 차단 페이지 */
class BlockGuideFragment: Fragment() {
    val binding by lazy {
        FragmentBlockGuideBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity?.findViewById<View>(R.id.main_top_bar))?.visibility = View.GONE
        (activity?.findViewById<View>(R.id.main_nav))?.visibility = View.GONE


        val aiPlanId = requireArguments().getInt("aiPlanId")


        /* 넘어가기 */
        binding.skipBtn.setOnClickListener {
            // 실행 중 화면으로 이동
            Log.d("BlockGuideFragment", "포그라운드 서비스 X")
            moveFragment(ScheduleExecutionFragment(), aiPlanId, false)
        }
        /* 진행 중 화면 */
        binding.nextBtn.setOnClickListener {
            Log.d("BlockGuideFragment", "포그라운드 서비스 O")
            moveFragment(ScheduleExecutionFragment(), aiPlanId, true)
        }

        initClickListener()


    }
    private fun moveFragment(fragment: Fragment, aiPlanId: Int, blockCheck: Boolean){
        val spf = requireContext().getSharedPreferences("executedTask", MODE_PRIVATE)
        spf.edit().putInt("taskId", aiPlanId).commit()

        Log.d("BlockGuideFragment", "보내는 아이디: ${aiPlanId}")
        val bundle = Bundle()
        bundle.putBoolean("blockCheck", blockCheck)
        fragment.arguments = bundle
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_frm, fragment)
            commit()
        }
    }


    private fun initClickListener(){
        /* 뒤로가기 */
        binding.toolBackClose1.blockBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        /* 닫기 */
        binding.toolBackClose1.blockClose.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}