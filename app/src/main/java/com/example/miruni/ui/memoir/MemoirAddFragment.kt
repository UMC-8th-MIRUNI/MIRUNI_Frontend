package com.example.miruni.ui.memoir

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miruni.R
import com.example.miruni.api.ApiService
import com.example.miruni.api.getRetrofit
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.data.Task
import com.example.miruni.databinding.FragmentMemoirAddBinding
import com.example.miruni.ui.homepage.t
import kotlinx.coroutines.launch


// 회고 날짜의 회고 목록 조회api연결
class MemoirAddFragment: Fragment() {
    val binding by lazy {
        FragmentMemoirAddBinding.inflate(layoutInflater)
    }
    private lateinit var taskDB : ScheduleDatabase
    private lateinit var taskDatas : List<Task>
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


        lifecycleScope.launch {
            /*
            특정날짜 회고 보여주기
            taskData 대신 adapter에 연결
             */
            try{
                val token = "Bearer $t"
                date = arguments?.getString("date").toString()

                val api = getRetrofit().create(ApiService::class.java)
                val response = api.memoirDateList(token, date)

                Log.d("특정 날짜 회고 목록 조회", "성공: ${response}")

                val body = response.body()
                Log.d("특정 날짜 회고 목록 조회","목록 아이디 조회: ${body?.result?.get(0)?.id}")

                taskDB = ScheduleDatabase.getInstance(requireContext())!!
                taskDatas = taskDB.taskDao().getTask()
            }catch (e: Exception){
                Log.e("특정 날짜 회고 목록 조회", "에러발생: ${e.message}")
            }

            // 단일 회고 상세 조회로 reviewId 넘겨주기
            val dapter = MemoirAddRVAdapter(taskDatas) { reviewId ->
                // reviewId MemoirCompleteFragment로 넘기기
                val bundle = Bundle()
                bundle.putInt("reviewId", reviewId)

                val transaction = parentFragmentManager.beginTransaction()
                val fragment = MemoirCompleteFragment()
                fragment.arguments = bundle

                transaction.replace(R.id.process_frm, fragment)
                transaction.addToBackStack(null)
                transaction.commit()

            }

            binding.memeoirAddRv.adapter = dapter
            binding.memeoirAddRv.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.memoirAddBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.process_frm, MemoirWriteFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
        binding.backBtn.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}