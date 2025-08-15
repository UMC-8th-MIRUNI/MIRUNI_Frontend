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
import com.example.miruni.RVSpacer
import com.example.miruni.TokenManager
import com.example.miruni.api.ApiService
import com.example.miruni.api.model.MemoirCountResponse
import com.example.miruni.api.model.ReviewDate
import com.example.miruni.api.getRetrofit
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.databinding.FragmentMemoirListBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

private lateinit var memoirAdapter: MemoirListRVAdapter
private lateinit var countResponse: Response<MemoirCountResponse>
private var body: List<ReviewDate> = emptyList()
private val api = getRetrofit().create(ApiService::class.java)
private lateinit var token: String
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

        (activity?.findViewById<View>(R.id.main_top_bar))?.visibility = View.GONE // 상단바 없애기

        lifecycleScope.launch {
            memoirCountByDate() // 날짜별 회고록 갯수 조회
        }

        /* 검색하기 */
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
                            memoirSearch(query)
                        }
                    }else{
                        Toast.makeText(requireContext(), "날짜를 입력하세요", Toast.LENGTH_SHORT).show()
                    }
                    true
                }else false
            }
        }
    }


    /* 날짜별 회고록 갯수 조회 API 연동*/
    private suspend fun memoirCountByDate(){
        try {
            token = String.format("Bearer ${TokenManager.getToken(requireContext())}")
            countResponse = api.memoirCountByDate(token)
            if (countResponse.isSuccessful) {
                Log.d("날짜 별 회고록 갯수 조회", "연결 성공: ${countResponse}")

                body = countResponse?.body()?.result ?: emptyList()
                setRecyclerView(body)

            } else {
                Log.e(
                    "날짜 별 회고록 갯수 조회",
                    "실패: ${countResponse.code()} - ${countResponse.message()}"
                )
            }
        } catch (e: Exception) {
            Log.e("날짜 별 회고록 갯수 조회", "에러: ${e.message}")
        }
    }

    /* 회고 날짜 검색 조회 API 연동 */
    private suspend fun memoirSearch(query: String){
        // 회고 날짜 검색 조회
        Toast.makeText(requireContext(), "검색: $query", Toast.LENGTH_SHORT).show()
        try {
            val searchResponse = api.memoirSearch(token, query)

            Log.d("회고 날짜 검색 조회", "연결성공: ${searchResponse}")

            db = ScheduleDatabase.getInstance(requireContext())!!
            val review = searchResponse.body()?.result ?: throw IllegalStateException("!! 회고날짜 검색 조회 body: null값 !!")

            val list = listOf(review)
            withContext(Dispatchers.Main) {
                setRecyclerView(list)
            }
        }catch (e: Exception){
            Log.e("회고 날짜 검색 조회", "에러: ${e.message}")
        }
    }

    /* 리사이클러뷰 이동 메서드 */
    private fun setRecyclerView(data: List<ReviewDate>){
        memoirAdapter = MemoirListRVAdapter(data){ date ->
            // MemoirAddFragment로 이동
            val fragment = MemoirAddFragment()

            val bundle = Bundle()
            bundle.putString("date", date)
            fragment.arguments = bundle

            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.main_frm, fragment)
                addToBackStack(null)
                commit()
            }

        }
        val spacer = resources.getDimensionPixelSize(R.dimen.recycler_dimen)

        binding.memoirRV.apply {
            adapter = memoirAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            if(itemDecorationCount == 0)
                addItemDecoration(RVSpacer(spacer))
        }
    }

}
