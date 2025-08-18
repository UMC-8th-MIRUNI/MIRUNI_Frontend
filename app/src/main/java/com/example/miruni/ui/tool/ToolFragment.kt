package com.example.miruni.ui.tool

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.miruni.MainActivity
import com.example.miruni.R
import com.example.miruni.databinding.FragmentToolBinding

class ToolFragment: Fragment() {
    val binding by lazy {
        FragmentToolBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /** 상단바 색상 변경 **/
        (activity as? MainActivity)?.setTopBarColor(R.color.white)

        // 클릭 버튼
        initClickListener()

        }


    /* 버튼 초기화 */
    private fun initClickListener(){
        binding.toolBlock.setOnClickListener {
            /* 방해 요소 차단 이동*/
            moveFragment(BlockGuideFragment())

        }
        binding.toolSplit.setOnClickListener {
            /* 일정 쪼개기 이동*/
        }
    }

    /* 프레그먼트 간 이동 */
    private fun moveFragment(fragment: Fragment){
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, fragment)
            .addToBackStack(null)
            .commit()
    }

}