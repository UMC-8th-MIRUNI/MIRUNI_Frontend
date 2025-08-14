package com.example.miruni.ui.homepage

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miruni.MainActivity
import com.example.miruni.R
import com.example.miruni.TextVPAdapter
import com.example.miruni.api.ApiService
import com.example.miruni.api.getRetrofit
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.data.Task
import com.example.miruni.data.repository.HomepageRepository
import com.example.miruni.databinding.FragmentHomepageBinding
import com.example.miruni.databinding.LayoutCheckpopupBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomepageFragment: Fragment() {
    private lateinit var binding: FragmentHomepageBinding
    private var datas = arrayListOf<Task>()
    private lateinit var db: ScheduleDatabase
    private lateinit var adapter: HomepageRVAdapter
    private lateinit var viewModel: HomepageViewModel

    private var deleteTaskId: List<Int> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomepageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /** 상단바 색상 변경 **/
        (activity as? MainActivity)?.setTopBarColor(R.color.main)


        connectAdapter()
        clickEvent()
        dataBind()

        var motive = ""
        lateinit var taskList: List<Task>

        binding.motivationTxt.text = motive

        // 명언 더미데이터 + viewPager연결
        val dummyData = listOf("이러쿵", "저러쿵", "화이팅~")

        // viewPager + dotsIndicator연결
        val VPAdapter = TextVPAdapter(dummyData)
        binding.helloViewpager.adapter = VPAdapter
        binding.dotsIndicator.attachTo(binding.helloViewpager)
    }

    // 데이터 매개변수로 받아오기
    private fun connectAdapter() {

        val token = "Bearer $t"
        val repository = HomepageRepository()
        val factory = HomepageViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HomepageViewModel::class.java]
        viewModel.loadHomepage(token)

        viewModel.homepagedatas.observe(viewLifecycleOwner) { datas ->

        }

        // 오늘의 일정 adapter연결
        val layoutManager = GridLayoutManager(requireContext(), 5, GridLayoutManager.HORIZONTAL, false)
        adapter = HomepageRVAdapter(datas)

        binding.homepageRecyclerView.layoutManager = layoutManager
        binding.homepageRecyclerView.adapter = adapter

        adapter.setOnClickListener(object : HomepageRVAdapter.onplayClickListener {
            override fun onPlayClick(isDone: String) {
                TODO("Not yet implemented")
            }
            override fun onDeleteItem(seletedItems: List<Int>) {
                deleteTaskId = seletedItems
            }
        })

    }

    private fun clickEvent() {
        // 중지버튼 눌렀을 때
        binding.homepageFailBtn.setOnClickListener {
            val list = datas.filter { it.status == "fail" }

            // 다시 dapter에 연결
        }
        // 완료버튼 눌렀을 때
        binding.homepageCompleteBtn.setOnClickListener {

        }
        // 예정 버튼 눌렀을 때
        binding.homepageExpectedBtn.setOnClickListener {

        }
        // 전체 버튼 눌렀을 때
        binding.homepageAllBtn.setOnClickListener {

        }

        // 다가오는 일정 play 실행
        binding.taskPlay.setOnClickListener {

        }
        // 삭제하기 버튼 눌렀을 때
        binding.taskDeleteBtn.setOnClickListener {
            binding.taskDeleteCompleteBtn.visibility = VISIBLE
            binding.taskDeleteBtn.visibility = View.GONE

            adapter.deleteItem(true)
        }
        // 삭제완료 버튼 눌렀을 때
        binding.taskDeleteCompleteBtn.setOnClickListener {
            binding.taskDeleteBtn.visibility = VISIBLE
            binding.taskDeleteCompleteBtn.visibility = View.GONE

            // 삭제 재확인 Dialog 띄우기
            val popupBinding = LayoutCheckpopupBinding.inflate(LayoutInflater.from(requireContext()))
            val dialog = Dialog(requireContext())
            dialog.setContentView(popupBinding.root)
            dialog.show()

            popupBinding.deleteNo.setOnClickListener {
                dialog.hide()
                adapter.deleteItem(false)
            }
            popupBinding.deleteYes.setOnClickListener {
                // 삭제 api연결
                // deleteTaskId 활용
                for(task in deleteTaskId){

                }

                dialog.hide()
                adapter.deleteItem(false)
            }

        }
    }

    fun dataBind() {
        binding.username = "김가영"
        binding.taskCount = "${5}"
        binding.achievement = "${78}"
    }



}


