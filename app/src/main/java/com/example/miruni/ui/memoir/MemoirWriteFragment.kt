package com.example.miruni.ui.memoir

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.miruni.R
import com.example.miruni.TokenManager
import com.example.miruni.api.ApiService
import com.example.miruni.api.getRetrofit
import com.example.miruni.api.model.MemoirSaveRequest
import com.example.miruni.data.Mood
import com.example.miruni.data.Review
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.data.repository.HomepageRepository
import com.example.miruni.data.repository.MemoirRepository
import com.example.miruni.databinding.FragmentMemoirWriteBinding
import com.example.miruni.ui.homepage.HomepageViewModel
import com.example.miruni.ui.homepage.HomepageViewModelFactory
import com.google.gson.Gson
import kotlinx.coroutines.launch

/* 아무것도 안쓰고 확인 버튼 누르면 강종됨 */
class MemoirWriteFragment: Fragment() {
    val binding by lazy {
        FragmentMemoirWriteBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }
    private var currentMood: Mood? = null
    private lateinit var db: ScheduleDatabase
    private var review : Review? = null

    private var aiPlanId: Int? = null
    private var planId = -1
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // 표정 클릭
        moodClick()

        db = ScheduleDatabase.getInstance(requireContext()) ?: throw IllegalStateException("DB 생성 실패")
        val item = db.planDao().getPlan(arguments?.getInt("aiPlanId") ?: -1)

        aiPlanId = arguments?.getInt("aiPlanId")
        if(aiPlanId == -1) aiPlanId=null
        planId = requireArguments().getInt("planId")

        val repository = HomepageRepository()
        val factory = HomepageViewModelFactory(repository)
        val viewModel = ViewModelProvider(requireActivity(), factory)[HomepageViewModel::class.java]
        val token = String.format("Bearer ${TokenManager.getToken(requireContext())}")

        viewModel.getSchedule(token, planId)
        viewModel.scheduleData.observe(viewLifecycleOwner) { data ->
            for(i in data.plans){
                if(aiPlanId == i.planId){
                    binding.writeLayout.memoirTitle.memoirWriteTitle.text = i.description
                    val startTime = i.startTime.split(":")
                    binding.writeLayout.memoirTitle.memoirWriteDate.text = "${i.date} ${startTime[0]}:${startTime[1]}"
                }
            }
        }

        initClickListener()
        //binding.writeLayout.memoirTitle

    }
    /* 회고 작성 후 저장 API 연동 */
    private fun memoirSave(){
        // 작성 값 저장 - request값 생성
        val registerReview = setMemoirRequest()
        val token = String.format("Bearer ${TokenManager.getToken(requireContext())}")
        val contentType = "application/json"

        val repository = MemoirRepository()
        val factory = MemoirViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[MemoirViewModel::class.java]
        viewModel.getMemoirSave(token, contentType, registerReview)
        viewModel.saveData.observe(viewLifecycleOwner){ data ->
            review = data.result ?: null

            if(review!= null){
                val review = Review(
                    id = review!!.id,
                    aiPlanId = review!!.aiPlanId,
                    planId = review!!.planId,
                    mood = review?.mood ?: Mood.HAPPY,
                    title = review?.title ?: "",
                    description = review?.description ?: "",
                    achievement = review?.achievement ?: 0,
                    memo = review?.memo ?: "",
                    createdAt = review?.createdAt ?: ""
                )
                binding.writeLayout.memoirTitle.memoirWriteTitle.text = review.title
                binding.writeLayout.memoirTitle.memoirDescription.text = review.description
                binding.writeLayout.memoirTitle.memoirWriteDate.text = review.createdAt
                // 앱 내에 저장
                db.reviewDao().insertReview(review)
            }
        }

        // 회고 작성 완료 페이지(MemoirCompleteFragment)로 이동
        val bundle = Bundle()
        bundle.putInt("reviewId", review?.id ?: 0)

        val fragment = MemoirCompleteFragment()
        fragment.arguments = bundle
        parentFragmentManager.beginTransaction().apply {
            replace(R.id.main_frm, fragment)
            commit()
        }
    }
    /* 클릭 이벤트 버튼 */
    private fun initClickListener(){

        binding.writeLayout.memoirWriteOk.setOnClickListener {
            // 회고 작성 후 저장 버튼
            lifecycleScope.launch {
                if(checkData()){
                    memoirSave()
                }
            }
        }

        binding.writeLayout.memoirTitle.memoirWriteBack.setOnClickListener {
            // 뒤로가기
            requireActivity().supportFragmentManager.popBackStack()
        }

    }
    fun showMood(mood: Mood): Mood? {
        val activeIcons = mapOf(
            Mood.SAD to binding.writeLayout.sadMiruniActive,
            Mood.RELAXED to binding.writeLayout.surprisedMiruniActive,
            Mood.HAPPY to binding.writeLayout.happyMiruniActive,
            Mood.ANGRY to binding.writeLayout.angryMiruniActive,
            Mood.ANXIOUS to binding.writeLayout.disappointedMiruniActive
        )
        val inactiveIcons = mapOf(
            Mood.SAD to binding.writeLayout.sadMiruniInactive,
            Mood.RELAXED to binding.writeLayout.surprisedMiruniInactive,
            Mood.HAPPY to binding.writeLayout.happyMiruniInactive,
            Mood.ANGRY to binding.writeLayout.angryMiruniInactive,
            Mood.ANXIOUS to binding.writeLayout.disappointedMiruniInactive
        )

        // 현재 선택된 감정을 다시 누른 경우 → 선택 취소
        if (currentMood == mood) {
            // 모두 active 숨기고, inactive 보이기
            activeIcons.values.forEach { it.visibility = View.INVISIBLE }
            inactiveIcons.values.forEach { it.visibility = View.VISIBLE }
            return null
        }

        // 전체 active/inactive 초기화
        activeIcons.values.forEach { it.visibility = View.INVISIBLE }
        inactiveIcons.values.forEach { it.visibility = View.VISIBLE }

        // 선택된 감정만 active 보이게, inactive 숨기기
        activeIcons[mood]?.visibility = View.VISIBLE
        inactiveIcons[mood]?.visibility = View.INVISIBLE

        return mood
    }
    /* 표정 클릭 */
    fun moodClick() : Mood{
        val moods = listOf(Mood.SAD, Mood.RELAXED, Mood.HAPPY, Mood.ANGRY, Mood.ANXIOUS)

        moods.forEach { mood ->
            val inactiveView = when (mood) {
                Mood.SAD -> binding.writeLayout.sadMiruniInactive
                Mood.RELAXED -> binding.writeLayout.surprisedMiruniInactive
                Mood.HAPPY -> binding.writeLayout.happyMiruniInactive
                Mood.ANGRY -> binding.writeLayout.angryMiruniInactive
                Mood.ANXIOUS -> binding.writeLayout.disappointedMiruniInactive
                else -> binding.writeLayout.inactiveMood
            }

            inactiveView.setOnClickListener {
                currentMood = showMood(mood)
            }

            // active 이미지도 클릭하면 선택 해제되게
            val activeView = when (mood) {
                Mood.SAD -> binding.writeLayout.sadMiruniActive
                Mood.RELAXED -> binding.writeLayout.surprisedMiruniActive
                Mood.HAPPY-> binding.writeLayout.happyMiruniActive
                Mood.ANGRY -> binding.writeLayout.angryMiruniActive
                Mood.ANXIOUS -> binding.writeLayout.disappointedMiruniActive
                else -> binding.writeLayout.activeMood
            }

            activeView.setOnClickListener {
                currentMood = showMood(mood)
            }
        }
        return currentMood ?: Mood.ANXIOUS
    }

    /* 데이터 맞는지 확인 */
    fun checkData() : Boolean {
        val mood = currentMood ?: Mood.NOTHING
        val achievement = binding.writeLayout.archievePercent.text.toString().toInt()
        val memo = binding.writeLayout.memoirWriteTxt.text.toString()

        if(mood == Mood.NOTHING) {
            Toast.makeText(requireContext(), "감정을 선택해주세요!", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(achievement == null) {
            Toast.makeText(requireContext(), "성취도를 입력해주세요!", Toast.LENGTH_SHORT).show()
            return false
        }
        else if(memo == null) {
            Toast.makeText(requireContext(), "메모를 입력해주세요!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
    /* request 응답 생성 */
    fun setMemoirRequest() : MemoirSaveRequest {
        val mood = currentMood ?: Mood.NOTHING
        val achievement = binding.writeLayout.archievePercent.text.toString().toInt()
        val memo = binding.writeLayout.memoirWriteTxt.text.toString()

        return MemoirSaveRequest(aiPlanId = aiPlanId, planId = planId, mood = mood, achievement = achievement, memo = memo)
    }
}
