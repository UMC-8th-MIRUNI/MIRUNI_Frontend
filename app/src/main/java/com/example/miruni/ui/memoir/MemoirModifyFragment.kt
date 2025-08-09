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
import com.example.miruni.api.MemoirUpdateRequst
import com.example.miruni.api.getRetrofit
import com.example.miruni.data.Mood
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.databinding.FragmentMemoirModifyBinding
import com.example.miruni.ui.homepage.t
import kotlinx.coroutines.launch

// 회고 수정 api
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
    private var db = ScheduleDatabase.getInstance(requireContext())
    private var currentMood: Mood? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // 기존 회고 데이터 불러오기
        initLayout()
        var mood = arguments?.getString("mood") as Mood

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

                    val token = "Bearer $t"
                    val api = getRetrofit().create(ApiService::class.java)
                    val response = api.memoirUpadate(token, reviewId, request)

                    Log.d("회고 수정", "성공: ${response}")

                    // 회고 작성 완료 페이지(MemoirCompleteFragment)로 이동
                    val bundle = Bundle()

                    /**  후에 reponse로 받은걸로 넘겨야함  **/
                    bundle.putInt("reviewId", 2)
                    val fragment = MemoirCompleteFragment()
                    fragment.arguments = bundle
                    val transaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.process_frm, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()

                    if(response.isSuccessful){
                        val body = response.body()
                        Log.d("회고 수정", "수정된 메모: ${body?.result?.memo}")
                    }else{
                        Log.e("회고 수정", "reponse 실패: ${response.code()} - ${response.message()}")
                    }
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

        val review = db?.reviewDao()?.findReviewById(id)

        binding.modifyLayout.memoirWriteTitle.text = review?.title ?: ""
        binding.modifyLayout.archievePercent.setText(review?.achievement?.toString() ?: "0")
        binding.modifyLayout.archievePercent.setSelection(binding.modifyLayout.archievePercent.text.length)
        binding.modifyLayout.memoirWriteTxt.setText(review?.memo ?: "")
        Log.d("수정 내용 조회 확인", "메모내용: ${review?.memo}")
        binding.modifyLayout.memoirWriteTxt.setSelection(binding.modifyLayout.memoirWriteTxt.text.length)
        binding.modifyLayout.memoirWriteDate.text = review?.createdAt
        binding.modifyLayout.memoirDescription.text = arguments?.getString("description")

        /*binding.modifyLayout.memoirWriteTitle.text = arguments?.getString("title")
        binding.modifyLayout.archievePercent.setText(arguments?.getInt("achievement")!!.toString())
        binding.modifyLayout.archievePercent.setSelection(binding.modifyLayout.archievePercent.text.length)
        binding.modifyLayout.memoirWriteTxt.setText(arguments?.getString("memo"))
        Log.d("수정 내용 조회 확인", "메모내용: ${arguments?.getString("memo")}")
        binding.modifyLayout.memoirWriteTxt.setSelection(binding.modifyLayout.memoirWriteTxt.text.length)
        binding.modifyLayout.memoirWriteDate.text = arguments?.getString("createdAt")
        binding.modifyLayout.memoirDescription.text = arguments?.getString("description")*/
    }
    fun showMood(mood: Mood): Mood? {
        val activeIcons = mapOf(
            Mood.기쁨 to binding.modifyLayout.happyMiruniActive,
            Mood.불안 to binding.modifyLayout.disappointedMiruniActive,
            Mood.평온 to binding.modifyLayout.surprisedMiruniActive,
            Mood.분노 to binding.modifyLayout.angryMiruniActive,
            Mood.슬픔 to binding.modifyLayout.sadMiruniActive
        )
        val inactiveIcons = mapOf(
            Mood.기쁨 to binding.modifyLayout.happyMiruniInactive,
            Mood.불안 to binding.modifyLayout.disappointedMiruniInactive,
            Mood.평온 to binding.modifyLayout.surprisedMiruniInactive,
            Mood.분노 to binding.modifyLayout.angryMiruniInactive,
            Mood.슬픔 to binding.modifyLayout.sadMiruniInactive
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
                Mood.슬픔 -> binding.modifyLayout.sadMiruniInactive
                Mood.평온 -> binding.modifyLayout.surprisedMiruniInactive
                Mood.기쁨 -> binding.modifyLayout.happyMiruniInactive
                Mood.분노 -> binding.modifyLayout.angryMiruniInactive
                Mood.불안 -> binding.modifyLayout.disappointedMiruniInactive
            }

            inactiveView.setOnClickListener {
                currentMood = showMood(mood)
            }

            // active 이미지도 클릭하면 선택 해제되게
            val activeView = when (mood) {
                Mood.슬픔 -> binding.modifyLayout.sadMiruniActive
                Mood.평온 -> binding.modifyLayout.surprisedMiruniActive
                Mood.기쁨 -> binding.modifyLayout.happyMiruniActive
                Mood.분노 -> binding.modifyLayout.angryMiruniActive
                Mood.불안 -> binding.modifyLayout.disappointedMiruniActive
            }

            activeView.setOnClickListener {
                currentMood = showMood(mood)
            }
        }
        return currentMood ?: Mood.불안
    }
}
