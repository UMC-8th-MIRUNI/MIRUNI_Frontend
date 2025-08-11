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
import com.example.miruni.MainActivity
import com.example.miruni.R
import com.example.miruni.api.ApiService
import com.example.miruni.api.MemoirCountResponse
import com.example.miruni.api.ReviewDate
import com.example.miruni.api.getRetrofit
import com.example.miruni.data.Mood
import com.example.miruni.data.Review
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.databinding.FragmentMemoirListBinding
import com.example.miruni.ui.homepage.t
import kotlinx.coroutines.launch
import retrofit2.Response

// 날짜 별 회고록 갯수 조회 API ->
// 회고 날짜 검색 조회 API ->

private lateinit var adapter: MemoirListRVAdapter
private lateinit var countResponse: Response<MemoirCountResponse>
private var body: List<ReviewDate> = emptyList()
private lateinit var date: String
private val api = getRetrofit().create(ApiService::class.java)
private val token = "Bearer $t"
private lateinit var db: ScheduleDatabase
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** 상단바 색상 변경 **/
        (activity as? MainActivity)?.setTopBarColor(R.color.white)

        binding.listBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        val dummy = listOf(
            Review(0, null, 1, Mood.기쁨, "헐", "흑",100, "헉", "2025-02-23"),
            Review(1, null, 2, Mood.기쁨, "헐", "흑",100, "헉", "2025-02-23"),
            Review(2, null, 2, Mood.기쁨, "헐", "흑",100, "헉", "2025-02-23"),
            Review(3, null, 4, Mood.기쁨, "헐", "흑",100, "헉", "2025-02-23")
        )

        lifecycleScope.launch {
            // 날짜 별 회고록 갯수 조회
            try{
                countResponse = api.memoirCountByDate(token)
                if(countResponse.isSuccessful){
                    Log.d("날짜 별 회고록 갯수 조회", "연결 성공: ${countResponse}")
                }else{
                    Log.e("날짜 별 회고록 갯수 조회", "실패: ${countResponse.code()} - ${countResponse.message()}")
                }
                val list = countResponse.body()?.result
                if (!list.isNullOrEmpty()) {
                    for(i in list){
                        Log.d("날짜 별 회고록 갯수 조회", "리스트확인: ${i.date} - ${i.count}")
                    }
                }else{
                    Log.d("날짜 별 회고록 갯수 조회", "갯수가 null입니다")
                }
                body = countResponse.body()?.result ?: throw IllegalStateException("!! 날짜 별 회고록 갯수 조회 body: null값 !!")
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



        adapter = MemoirListRVAdapter(body)
        binding.memoirRV.adapter = adapter
        binding.memoirRV.layoutManager = GridLayoutManager(requireContext(), 2)

        adapter.setOnItemClick(object : MemoirListRVAdapter.OnMemoirItemClick{
            override fun onItemClick(date: String) {
                // MemoirAddFragment로 이동
                val fragment = MemoirAddFragment()

                val bundle = Bundle()
                bundle.putString("date", date)
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
                val searchResponse = api.memoirSearch(token, query)

                Log.d("회고 날짜 검색 조회", "연결성공: ${searchResponse}")


                db = ScheduleDatabase.getInstance(requireContext())!!
                val review = searchResponse.body()?.result ?: throw IllegalStateException("!! 회고날짜 검색 조회 body: null값 !!")

                val list = listOf(
                    review
                )
                Log.d("회고 날짜 검색 조회", "${review.date} - ${review.count}")
                adapter = MemoirListRVAdapter(list)
                binding.memoirRV.adapter = adapter
                binding.memoirRV.layoutManager = GridLayoutManager(requireContext(), 2)
            }
        }catch (e: Exception){
            Log.e("회고 날짜 검색 조회", "에러: ${e.message}")
        }
    }
}
