package com.example.miruni.ui.memoir

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miruni.MainActivity
import com.example.miruni.R
import com.example.miruni.RVSpacer
import com.example.miruni.TokenManager
import com.example.miruni.api.ApiService
import com.example.miruni.api.getRetrofit
import com.example.miruni.api.model.ReviewByDate
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.data.Task
import com.example.miruni.databinding.FragmentMemoirAddBinding
import kotlinx.coroutines.launch
import retrofit2.Response

private lateinit var taskDB : ScheduleDatabase
private lateinit var taskDatas : List<Task>
private lateinit var body: List<ReviewByDate>

class MemoirAddFragment: Fragment() {
    val binding by lazy {
        FragmentMemoirAddBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }
    private lateinit var date: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity?.findViewById<View>(R.id.main_top_bar))?.visibility = View.GONE   // 상단 바 없애기

        binding.memoirAddDate.text = arguments?.getString("date")   // MemoirFragment에서 받아 온 bundle값

        lifecycleScope.launch {
            memoirDateList() // 특정 날짜 회고 목록 조회
        }

        initClickListener() // 클릭 이벤트


    }

    /* 버튼 클릭 이벤트 함수*/
    private fun initClickListener(){

        binding.memoirAddBtn.setOnClickListener {
            // 회고 작성 페이지로 이동
            moveFragment(MemoirWriteFragment())
        }

        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack() // 뒤로가기
        }
    }

    /* 프레그먼트 이동 함수 */
    private fun moveFragment(fragment: Fragment){
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_frm, fragment)
            addToBackStack(null)
            commit()
        }
    }

    /* 특정 날짜 회고 목록 조회 API 연동*/
    private suspend fun memoirDateList(){
            try{
                val token = String.format("Bearer ${TokenManager.getToken(requireContext())}")
                date = arguments?.getString("date").toString()
                Log.d("특정 날짜 회고 목록 조회", "요청 날짜: $date")
                val api = getRetrofit().create(ApiService::class.java)
                val response = api.memoirDateList(token, date)

                Log.d("특정 날짜 회고 목록 조회", "성공: ${response}")

                body = response.body()?.result ?: throw IllegalStateException("!! 특정 날짜 회고 목록 조회 안됨 !!")

                taskDB = ScheduleDatabase.getInstance(requireContext())!!
                taskDatas = taskDB.taskDao().getTask()
            }catch (e: Exception){
                Log.e("특정 날짜 회고 목록 조회", "에러발생: ${e.message}")
            }

            // 단일 회고 상세 조회로 reviewId 넘겨주기
            val reviewAapter = MemoirAddRVAdapter(body) { reviewId ->
                // reviewId MemoirCompleteFragment로 넘기기
                val bundle = Bundle()
                bundle.putInt("reviewId", reviewId)

                val fragment = MemoirCompleteFragment()
                fragment.arguments = bundle

                moveFragment(fragment)
            }

        /* 리사이클러뷰 연결 */
        binding.memeoirAddRv.apply {
            adapter = reviewAapter
            layoutManager = LinearLayoutManager(requireContext())

            if(itemDecorationCount == 0) {
                val spacer = resources.getDimensionPixelSize(R.dimen.recycler_dimen)
                addItemDecoration(RVSpacer(spacer))
            }
        }
    }

}
