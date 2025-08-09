package com.example.miruni.ui.memoir

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.miruni.R
import com.example.miruni.api.ApiService
import com.example.miruni.api.getRetrofit
import com.example.miruni.data.Mood
import com.example.miruni.data.Review
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.databinding.FragmentMemoirListBinding
import com.example.miruni.ui.homepage.t
import kotlinx.coroutines.launch

// 날짜 별 회고록 갯수 조회 API -> 연결 성공
// 회고 날짜 검색 조회 API ->

class MemoirListFragment: Fragment() {
    val binding by lazy {
        FragmentMemoirListBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }
    private lateinit var date: String
    private val api = getRetrofit().create(ApiService::class.java)
    private val t = "Bearer ${com.example.miruni.ui.homepage.t}"
    private lateinit var db: ScheduleDatabase

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dummy = listOf(
            Review(0, null, 1, Mood.기쁨, "헐", 100, "헉", "2025-02-23"),
            Review(1, null, 2, Mood.기쁨, "헐", 100, "헉", "2025-02-23"),
            Review(2, null, 3, Mood.기쁨, "헐", 100, "헉", "2025-02-23"),
            Review(3, null, 4, Mood.기쁨, "헐", 100, "헉", "2025-02-23")
        )

        lifecycleScope.launch {
            // 날짜 별 회고록 갯수 조회
            try{
                val countResponse = api.memoirCountByDate(t)
                Log.d("날짜 별 회고록 갯수 조회", "연결성공: ${countResponse}")
                val body = countResponse.body()?.result?.size
            }catch (e: Exception){
                Log.e("날짜 별 회고록 갯수 조회", "에러: ${e.message}")
            }


                binding.memoirSearch.apply {
                    // 키보드에서 검색 버튼 표시
                    imeOptions = EditorInfo.IME_ACTION_SEARCH
                    inputType = InputType.TYPE_CLASS_DATETIME

                    // 검색 버튼 클릭 이벤트 처리
                    setOnEditorActionListener { v, actionId, event ->
                        if(actionId == EditorInfo.IME_ACTION_SEARCH){
                            val query = text.toString().trim()
                            if(query.isNotEmpty()){
                                lifecycleScope.launch {
                                    // 검색 함수 호출
                                    search(query)
                                }
                            }else{
                                Toast.makeText(requireContext(), "날짜를 입력하세요", Toast.LENGTH_SHORT).show()
                            }
                            true
                        }else false
                    }
                }

        }



        val adapter = MemoirListRVAdapter(dummy)
        binding.memoirRV.adapter = adapter
        binding.memoirRV.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter.setOnItemClick(object : MemoirListRVAdapter.OnMemoirItemClick{
            override fun onItemClick(review: Review) {
                // MemoirAddFragment로 이동
                val fragment = MemoirAddFragment()

                val bundle = Bundle()
                date = bundle.getString("date") ?: ""
                fragment.arguments = bundle

                val transaction = requireActivity().supportFragmentManager.beginTransaction()
                transaction.replace(R.id.process_frm, fragment)
                transaction.addToBackStack(null)
                transaction.commit()
            }

        })
    }
    private fun search(query: String){
        // 회고 날짜 검색 조회
        Toast.makeText(requireContext(), "검색: $query", Toast.LENGTH_SHORT).show()
        try {
            //date = binding.memoirSearch.text.toString()
            lifecycleScope.launch {
                val searchResponse = api.memoirSearch(t, query)

                Log.d("회고 날짜 검색 조회", "연결성공: ${searchResponse}")


                db = ScheduleDatabase.getInstance(requireContext())!!
                val review = db.reviewDao().findReviewByDate(searchResponse.body()?.result!!.date)
                val list = listOf(
                    review
                )
                val adapter = MemoirListRVAdapter(list)
                binding.memoirRV.adapter = adapter
                binding.memoirRV.layoutManager = GridLayoutManager(requireContext(), 2)
            }
        }catch (e: Exception){
            Log.e("회고 날짜 검색 조회", "에러: ${e.message}")
        }
    }
}
