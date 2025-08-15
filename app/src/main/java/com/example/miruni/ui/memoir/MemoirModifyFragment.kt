package com.example.miruni.ui.memoir

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.miruni.R
import com.example.miruni.TokenManager
import com.example.miruni.api.ApiService
import com.example.miruni.api.model.MemoirUpdateRequst
import com.example.miruni.api.getRetrofit
import com.example.miruni.data.Mood
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.databinding.FragmentMemoirModifyBinding
import kotlinx.coroutines.launch

class MemoirModifyFragment: Fragment(){

    val binding by lazy {
        FragmentMemoirModifyBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }
    private lateinit var db : ScheduleDatabase
    private var currentMood: Mood? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // 기존 회고 데이터 불러오기
        initLayout()

        moodClick()

        // 수정완료 버튼
        binding.modifyLayout.memoirWriteOk.setOnClickListener {
            // api request
            lifecycleScope.launch {
                try{
                    var reviewId = arguments?.getInt("reviewId") ?: 0
                    var mood = moodClick()

                    var achievement = binding.modifyLayout.archievePercent.text.toString().toInt()
                    val memo = binding.modifyLayout.memoirWriteTxt.text.toString()

                    val request = MemoirUpdateRequst(mood, achievement, memo)

                    val token = String.format("Bearer ${TokenManager.getToken(requireContext())}")
                    val api = getRetrofit().create(ApiService::class.java)
                    val response = api.memoirUpadate(token, reviewId, request)

                    Log.d("회고 수정", "성공: ${response}")

                    // 회고 작성 완료 페이지(MemoirCompleteFragment)로 이동
                    val bundle = Bundle()

                    bundle.putInt("reviewId", reviewId)
                    val fragment = MemoirCompleteFragment()
                    fragment.arguments = bundle
                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.main_frm, fragment)
                    transaction.commit()

                }catch (e: Exception){
                    Log.e("회고 수정" ,"에러: ${e.message}")
                }

            }

        }



        super.onViewCreated(view, savedInstanceState)
    }
    // 기존 회고 데이터 불러오기
    fun initLayout(){
        val id = arguments?.getInt("reviewId")!!
        db = ScheduleDatabase.getInstance(requireContext())!!
        val review = db?.reviewDao()?.findReviewById(id)

        /*binding.modifyLayout.memoirWriteTitle.text = review?.title ?: "title없어용"
        binding.modifyLayout.archievePercent.setText(review?.achievement?.toString() ?: "0")
        binding.modifyLayout.archievePercent.setSelection(binding.modifyLayout.archievePercent.text.length)
        binding.modifyLayout.memoirWriteTxt.setText(review?.memo ?: "왜없지")
        Log.d("수정 내용 조회 확인", "메모내용: ${review?.memo}")
        binding.modifyLayout.memoirWriteTxt.setSelection(binding.modifyLayout.memoirWriteTxt.text.length)
        binding.modifyLayout.memoirWriteDate.text = review?.createdAt
        binding.modifyLayout.memoirDescription.text = review?.description*/

        binding.modifyLayout.memoirTitle.memoirWriteTitle.text = arguments?.getString("title")
        binding.modifyLayout.archievePercent.setText(arguments?.getInt("achievement")!!.toString())
        binding.modifyLayout.archievePercent.setSelection(binding.modifyLayout.archievePercent.text.length)
        binding.modifyLayout.memoirWriteTxt.setText(arguments?.getString("memo"))
        Log.d("수정 내용 조회 확인", "메모내용: ${arguments?.getString("memo")}")
        binding.modifyLayout.memoirWriteTxt.setSelection(binding.modifyLayout.memoirWriteTxt.text.length)
        binding.modifyLayout.memoirTitle.memoirWriteDate.text = arguments?.getString("createdAt")
        binding.modifyLayout.memoirTitle.memoirDescription.text = arguments?.getString("description")
    }
    fun showMood(mood: Mood): Mood? {
        val activeIcons = mapOf(
            Mood.HAPPY to binding.modifyLayout.happyMiruniActive,
            Mood.ANXIOUS to binding.modifyLayout.disappointedMiruniActive,
            Mood.RELAXED to binding.modifyLayout.surprisedMiruniActive,
            Mood.ANGRY to binding.modifyLayout.angryMiruniActive,
            Mood.SAD to binding.modifyLayout.sadMiruniActive
        )
        val inactiveIcons = mapOf(
            Mood.HAPPY to binding.modifyLayout.happyMiruniInactive,
            Mood.ANXIOUS to binding.modifyLayout.disappointedMiruniInactive,
            Mood.RELAXED to binding.modifyLayout.surprisedMiruniInactive,
            Mood.ANGRY to binding.modifyLayout.angryMiruniInactive,
            Mood.SAD to binding.modifyLayout.sadMiruniInactive
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
        val moods = listOf(Mood.SAD, Mood.RELAXED, Mood.HAPPY, Mood.ANGRY, Mood.ANXIOUS)

        moods.forEach { mood ->
            val inactiveView = when (mood) {
                Mood.SAD -> binding.modifyLayout.sadMiruniInactive
                Mood.RELAXED -> binding.modifyLayout.surprisedMiruniInactive
                Mood.HAPPY -> binding.modifyLayout.happyMiruniInactive
                Mood.ANGRY -> binding.modifyLayout.angryMiruniInactive
                Mood.ANXIOUS -> binding.modifyLayout.disappointedMiruniInactive
            }

            inactiveView.setOnClickListener {
                currentMood = showMood(mood)
            }

            // active 이미지도 클릭하면 선택 해제되게
            val activeView = when (mood) {
                Mood.SAD -> binding.modifyLayout.sadMiruniActive
                Mood.RELAXED -> binding.modifyLayout.surprisedMiruniActive
                Mood.HAPPY -> binding.modifyLayout.happyMiruniActive
                Mood.ANGRY -> binding.modifyLayout.angryMiruniActive
                Mood.ANXIOUS -> binding.modifyLayout.disappointedMiruniActive
            }

            activeView.setOnClickListener {
                currentMood = showMood(mood)
            }
        }
        return currentMood ?: Mood.ANXIOUS
    }
}

