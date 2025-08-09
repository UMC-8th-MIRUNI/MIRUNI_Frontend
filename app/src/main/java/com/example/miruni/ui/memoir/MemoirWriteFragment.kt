package com.example.miruni.ui.memoir

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.miruni.R
import com.example.miruni.api.ApiService
import com.example.miruni.api.MemoirSaveRequest
import com.example.miruni.api.MemoirSaveResponse
import com.example.miruni.api.getRetrofit
import com.example.miruni.data.Mood
import com.example.miruni.data.Review
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.databinding.FragmentMemoirWriteBinding
import com.example.miruni.ui.homepage.t
import com.google.gson.Gson
import kotlinx.coroutines.launch

// 회고 작성 후 저장 api연결
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
    private var body: MemoirSaveResponse? = null
    private lateinit var db: ScheduleDatabase
    private var id = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // 표정 클릭
        moodClick()

        db = ScheduleDatabase.getInstance(requireContext()) ?: throw IllegalStateException("DB 생성 실패")

        binding.writeLayout.memoirWriteOk.setOnClickListener {
            // 등록 요청 fragment
            lifecycleScope.launch {
                try {
                    val registerReview = setMemoirRequest()

                    val token = "Bearer $t"
                    val contentType = "application/json"
                    val api = getRetrofit().create(ApiService::class.java)
                    val response = api.memoirSave(token, contentType, registerReview)

                    Log.d("회고 작성 후 저장", "성공: $response")

                    // 정보 받아서 저장
                    if(response.isSuccessful){
                        val review = response.body()?.result ?: null

                        if(review!= null){
                            val review = Review(
                                id = review.id,
                                aiPlanId = review.aiPlanId,
                                planId = review.planId,
                                mood = review.mood,
                                title = review.title,
                                achievement = review.achievement,
                                memo = review.memo,
                                createdAt = review.createdAt
                            )
                            id = review.id
                            // 앱 내에 저장
                            db.reviewDao().insertReview(review)
                        }
                        Log.d("회고 작성 후 저장", "저장된 메모: ${body?.result?.memo}")
                    }else{
                        Log.e("회고 작성 후 저장", "reponse실패: ${response.code()} - ${response.message()}")
                    }

                    // 회고 작성 완료 페이지(MemoirCompleteFragment)로 이동
                    val bundle = Bundle()

                    /**  후에 reponse로 받은걸로 넘겨야함  **/
                    bundle.putInt("reviewId", id)

                    val fragment = MemoirCompleteFragment()
                    fragment.arguments = bundle
                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.process_frm, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()

                }catch (e: Exception){
                    Log.e("회고 작성 후 저장", "에러: ${e.message}")
                }

            }

        }
        binding.writeLayout.memoirWriteBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    fun showMood(mood: Mood): Mood? {
        val activeIcons = mapOf(
            Mood.기쁨 to binding.writeLayout.happyMiruniActive,
            Mood.불안 to binding.writeLayout.disappointedMiruniActive,
            Mood.평온 to binding.writeLayout.surprisedMiruniActive,
            Mood.분노 to binding.writeLayout.angryMiruniActive,
            Mood.슬픔 to binding.writeLayout.sadMiruniActive
        )
        val inactiveIcons = mapOf(
            Mood.기쁨 to binding.writeLayout.happyMiruniInactive,
            Mood.불안 to binding.writeLayout.disappointedMiruniInactive,
            Mood.평온 to binding.writeLayout.surprisedMiruniInactive,
            Mood.분노 to binding.writeLayout.angryMiruniInactive,
            Mood.슬픔 to binding.writeLayout.sadMiruniInactive
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

    fun moodClick() : Mood{
        val moods = listOf(Mood.슬픔, Mood.평온, Mood.기쁨, Mood.분노, Mood.불안)

        moods.forEach { mood ->
            val inactiveView = when (mood) {
                Mood.슬픔 -> binding.writeLayout.sadMiruniInactive
                Mood.평온 -> binding.writeLayout.surprisedMiruniInactive
                Mood.기쁨 -> binding.writeLayout.happyMiruniInactive
                Mood.분노 -> binding.writeLayout.angryMiruniInactive
                Mood.불안 -> binding.writeLayout.disappointedMiruniInactive
            }

            inactiveView.setOnClickListener {
                currentMood = showMood(mood)
            }

            // active 이미지도 클릭하면 선택 해제되게
            val activeView = when (mood) {
                Mood.슬픔 -> binding.writeLayout.sadMiruniActive
                Mood.평온 -> binding.writeLayout.surprisedMiruniActive
                Mood.기쁨 -> binding.writeLayout.happyMiruniActive
                Mood.분노 -> binding.writeLayout.angryMiruniActive
                Mood.불안 -> binding.writeLayout.disappointedMiruniActive
            }

            activeView.setOnClickListener {
                currentMood = showMood(mood)
            }
        }
        return currentMood ?: Mood.불안
    }

    fun setMemoirRequest() : MemoirSaveRequest {

        // request body 생성
        // id겂 넘겨 받아야함
        val aiPlanId = 1
        val planId = 10
        val mood = currentMood ?: Mood.불안
        val achievement = binding.writeLayout.archievePercent.text.toString().toInt()
        val memo = binding.writeLayout.memoirWriteTxt.text.toString()

        Log.d("회고 작성 후 저장", Gson().toJson(MemoirSaveRequest(
            aiPlanId = 1,
            planId = 10,
            mood = currentMood ?: Mood.불안,
            achievement = binding.writeLayout.archievePercent.text.toString().toInt(),
            memo = binding.writeLayout.memoirWriteTxt.text.toString()
            )
        ))

        return MemoirSaveRequest(aiPlanId = aiPlanId, planId = planId, mood = mood, achievement = achievement, memo = memo)
    }
}