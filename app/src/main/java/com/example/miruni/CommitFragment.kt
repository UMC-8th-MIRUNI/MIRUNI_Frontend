package com.example.miruni

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.miruni.databinding.FragmentCommitBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.periodUntil

class CommitFragment : Fragment() {

    private lateinit var binding : FragmentCommitBinding
    private val loadingPage = LoadingFragment()

    private var scheduleList = arrayListOf<Triple<String, String, String>>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCommitBinding.inflate(layoutInflater, container, false)

        initClickListener()

        return binding.root
    }

    /**
     * 로딩 페이지 출력
     */
    private fun loading() {
        loadingPage.show(requireActivity().supportFragmentManager, loadingPage.tag)
    }

    private fun initClickListener() {
        /** 뒤로 가기 */
        binding.commitBackBtn.setOnClickListener {
            activity?.finish()
        }
        /** 등록 */
        binding.commitCommitBtn.setOnClickListener {
//            (context as ProcessingActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.process_frm, ScheduleFragment().apply {
//                    arguments = Bundle().apply {
//                        val gson = Gson()
//                        val scheduleJson = gson.toJson(schedule)
//                        putString("schedule", scheduleJson)
//                    }
//                })
//                .commitAllowingStateLoss()

            (context as ProcessingActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.process_frm, ScheduleFragment())
                .commitAllowingStateLoss()
        }
        /** 쪼개기 */
        binding.commitSplitBtn.setOnClickListener {
            // 데이터 서버로 보내기

            // 로딩 페이지 출력 / Thread 써야 하는지 확인
            runBlocking {
                loading()
                delay(1000L)
                loadingPage.dismiss()
            }

            // onResponse / onFailure 시 loadingPage.dismiss() 후
            // onResponse -> 다음 페이지로 연결
            initDummyData() // 서버로부터 전달받은 데이터를 처리
            val intent = Intent(context as ProcessingActivity, MainActivity::class.java)
            intent.putExtra("showFragment", "ScheduleFragment")
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            // onFailure -> 에러 처리
        }
    }

    /**
     * 백엔드에서 전달받은 쪼개진 값을 전달받아 가공
     */
    private fun initDummyData() {
        val startDate = LocalDate(2025, 5, 27)
        val endDate = LocalDate(2025, 5, 30)
        val todoPeriod = startDate.periodUntil(endDate)

        val startTime = LocalTime(19,0)
        val endTime = LocalTime(20,0)
        val todoTime = startTime.rangeUntil(endTime)

        scheduleList.addAll(
            listOf(
                Triple("5/27","초안 그리기, 스토리보드 작성","40분"),
                Triple("5/28","와이어프레임,IA 수정,템플릿 서치", "1시간"),
                Triple("5/29","기획안 Slides p.4-5까지 만들기", "1시간+"),
                Triple("5/30","기획안 Slides p.6-10까지 만들기", "1시간+")
            )
        )
    }

}