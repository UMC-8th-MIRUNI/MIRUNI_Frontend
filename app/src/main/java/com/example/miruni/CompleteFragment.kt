package com.example.miruni

import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.miruni.databinding.FragmentCompleteBinding
import androidx.core.graphics.toColorInt

class CompleteFragment : Fragment() {

    private lateinit var binding: FragmentCompleteBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCompleteBinding.inflate(layoutInflater, container, false)

        initPeriodText()
        initClickListener()

        return binding.root
    }

    /**
     * 이벤트 처리
     */
    private fun initClickListener() {
        binding.completeAcceptBtn.setOnClickListener {
            (context as ProcessingActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.process_frm, WritingReviewFragment())
                .commitAllowingStateLoss()
        }
    }

    /**
     * ##분 만에 미루니 성장 완료! -> ## 설정
     */
    private fun initPeriodText() {
        val textData: String = binding.completeMinuriGrowupTv.text.toString()
        Log.d("CompleteFragment: textData", textData)
        val spannableStringBuilder = SpannableStringBuilder(textData)

        val spaceIdx: Int = textData.indexOf(" ")
        Log.d("CompleteFragment: spaceIdx", spaceIdx.toString())
        val colorSpan = ForegroundColorSpan("#EF4444".toColorInt())
        spannableStringBuilder.setSpan(colorSpan, 0, spaceIdx, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.completeMinuriGrowupTv.text = spannableStringBuilder
    }
}