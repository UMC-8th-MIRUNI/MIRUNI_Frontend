package com.example.miruni
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.miruni.databinding.FragmentReviewBinding

class ReviewFragment : Fragment() {

    private lateinit var binding : FragmentReviewBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReviewBinding.inflate(layoutInflater, container, false)

        initData()
        initClickListener()

        return binding.root
    }

    private fun initClickListener(){
        /** 뒤로 가기 */
        binding.reviewBackIv.setOnClickListener {
            val intent = Intent(context as ProcessingActivity, MainActivity::class.java)
            intent.putExtra("showFragment", "CalendarFragment")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
        }

        /** 더보기 */
        binding.reviewMoreIv.setOnClickListener {

        }
    }

    /**
     * DB로부터 입력받은 데이터 값 넘겨받고 View에 표시
     */
    private fun initData(){

    }
}