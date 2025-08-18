package com.example.miruni.ui.memoir

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miruni.R
import com.example.miruni.RVSpacer
import com.example.miruni.TokenManager
import com.example.miruni.api.model.ReviewByDate
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.data.Task
import com.example.miruni.data.repository.MemoirRepository
import com.example.miruni.databinding.FragmentMemoirAddBinding

private lateinit var taskDB : ScheduleDatabase
private lateinit var taskDatas : List<Task>
private lateinit var body: List<ReviewByDate>
private lateinit var reviewAdapter: MemoirAddRVAdapter

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity?.findViewById<View>(R.id.main_top_bar))?.visibility = View.GONE   // 상단 바 없애기
        (activity?.findViewById<View>(R.id.main_nav))?.visibility = View.GONE // nav 없애기

        binding.memoirAddDate.text = arguments?.getString("date")   // MemoirFragment에서 받아 온 bundle값

        memoirDateList() // 특정 날짜 회고 목록 조회

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
    private fun memoirDateList(){
        val date = arguments?.getString("date").toString()

        val token = String.format("Bearer ${TokenManager.getToken(requireContext())}")
        val repository = MemoirRepository()
        val factory = MemoirViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[MemoirViewModel::class.java]

        viewModel.memoirDateList(token, date)
        viewModel.listData.observe(viewLifecycleOwner){ data ->
            body = data.result

            // 단일 회고 상세 조회로 reviewId 넘겨주기
                reviewAdapter = MemoirAddRVAdapter{ reviewId ->

                // reviewId MemoirCompleteFragment로 넘기기
                val bundle = Bundle()
                bundle.putInt("reviewId", reviewId)

                val fragment = MemoirCompleteFragment()
                fragment.arguments = bundle

                moveFragment(fragment)
            }

            /* 메뉴 클릭 이벤트 처리 (삭제) */
            reviewAdapter.setOnClickListener(object : MemoirAddRVAdapter.onClickMenuListener{
                override fun onClickMenu(reviewId: Int) {
                    viewModel.getMemoirDelete(token, reviewId)
                    body = body.filter { it.reviewId != reviewId }
                    reviewAdapter.initRecyclerView(body)
                }
            })


            /* 리사이클러뷰 연결 */
            binding.memeoirAddRv.apply {
                adapter = reviewAdapter
                layoutManager = LinearLayoutManager(requireContext())

                if(itemDecorationCount == 0) {
                    val spacer = resources.getDimensionPixelSize(R.dimen.recycler_dimen)
                    addItemDecoration(RVSpacer(spacer))
                }
            }
            reviewAdapter.initRecyclerView(body)
        }
    }
}
