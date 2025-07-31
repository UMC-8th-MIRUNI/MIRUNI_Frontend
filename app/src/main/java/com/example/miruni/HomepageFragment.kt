package com.example.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.miruni.data.Task
import com.example.miruni.databinding.FragmentHomepageBinding

class HomepageFragment: Fragment() {
    val binding by lazy {
        FragmentHomepageBinding.inflate(layoutInflater)
    }

    val dummyList = listOf(
        Task(0, "umc", "14:00", "[회계원리] 레포트 과제 (1)", "expected"),
        Task(1, "umc", "15:30", "[자료구조] 강의 정리", "fail"),
        Task(2, "umc", "17:00", "[UI/UX] 와이어프레임 작성", "complete"),
        Task(3, "umc", "15:30", "[회계원리] 레포트 과제 (1)", "expected"),
        Task(4, "umc", "14:00", "[UI/UX] 와이어프레임 작성", "delay"),
        Task(5, "umc", "14:00", "[자료구조] 강의 정리", "expected"),
        Task(6, "umc", "14:00", "[회계원리] 레포트 과제 (1)", "complete"),
        Task(7, "umc", "14:00", "[회계원리] 레포트 과제 (1)", "fail")
    )
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataBind()

        // 명언 더미데이터 + viewPager연결
        val dummyData = listOf("이러쿵", "저러쿵", "화이팅~")

        // viewPager + dotsIndicator연결
        val VPAdapter = TextVPAdapter(dummyData)
        binding.helloViewpager.adapter = VPAdapter
        binding.dotsIndicator.attachTo(binding.helloViewpager)

        // composeView 연결
        connectComposeView()
        // 버튼 클릭 이벤트
        clickEvent()
    }
    // 버튼 클릭 이벤트
    fun clickEvent(){

        binding.homepageCompleteBtn.setOnClickListener {
            val list = dummyList.filter { it.status == "complete" }
            binding.homepageTodayTask.setContent {
                todayTaskRV(datas = list)
            }
        }
        binding.homepageFailBtn.setOnClickListener {
            val list = dummyList.filter { it.status == "fail" }
            binding.homepageTodayTask.setContent {
                todayTaskRV(datas = list)
            }
        }
        binding.homepageDelayBtn.setOnClickListener {
            val list = dummyList.filter { it.status == "delay" }
            binding.homepageTodayTask.setContent {
                todayTaskRV(datas = list)
            }
        }
        binding.homepageExpectedBtn.setOnClickListener {
            val list = dummyList.filter{ it.status == "expected"}
            binding.homepageTodayTask.setContent {
                todayTaskRV(datas = list)
            }
        }
    }
    // composeView 연결
    fun connectComposeView(){
        binding.composeProcessBox.setContent {
            // 진행률 변수 임의 설정
            val progress = 80
            HomepageProcessBox(progress = progress)
        }
        binding.homepageNextSchedule.setContent {
            //val recentTask = dummyList.filter { it.date }
            HomepageNextBox()
        }
        binding.homepageTodayTask.setContent {
            todayTaskRV(datas = dummyList)
        }
    }
    // 데이터 바인딩 연결
    fun dataBind(){
        binding.username = "김가영"
        binding.taskCount = 5
    }
}