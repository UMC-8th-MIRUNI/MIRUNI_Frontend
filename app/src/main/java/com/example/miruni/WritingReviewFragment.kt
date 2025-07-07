package com.example.miruni

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.miruni.databinding.FragmentWritingReviewBinding

/**
 * MainActivity Context에서 구현
 */
class WritingReviewFragment : Fragment() {

    private lateinit var binding : FragmentWritingReviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWritingReviewBinding.inflate(layoutInflater, container, false)

        initClickListener()

        return binding.root
    }

    private fun initClickListener() {
        /** 뒤로 가기 */
        // -> 정확히 어디로 가는 것?
        binding.writingBackIv.setOnClickListener {
            val intent = Intent(context as ProcessingActivity, MainActivity::class.java)
            intent.putExtra("showFragment", "CalendarFragment")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }

        /** 완료 */
        binding.writingCompleteBtn.setOnClickListener {
            // DB에 업데이트 하는 과정 필요
            updateDB()

            (context as ProcessingActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.process_frm, ReviewFragment())
                .commitAllowingStateLoss()
        }
    }

    /**
     * DB에 업데이트
     */
    private fun updateDB() {

    }
}