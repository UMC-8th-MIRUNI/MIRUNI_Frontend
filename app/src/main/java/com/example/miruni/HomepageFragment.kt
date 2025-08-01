package com.example.miruni

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Dao
import com.example.miruni.databinding.FragmentHomepageBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomepageFragment: Fragment() {
    val binding by lazy {
        FragmentHomepageBinding.inflate(layoutInflater)
    }

    private var taskDatas = ArrayList<Task>()
    private lateinit var taskDB: TaskDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            taskDB = TaskDatabase.getInstance(requireContext())
            if(taskDB.taskDao().getTask().isEmpty()) {
                taskDB.taskDao().insertTask(Task(cheduleId = 0,startTime = "12:00", endTime ="14:00",content =  "[회계원리] 레포트 과제 (1)", status ="expected"))
                taskDB.taskDao().insertTask(Task(cheduleId =0,startTime ="11:00", endTime ="15:30", content ="[자료구조] 강의 정리",  status ="fail"))
                taskDB.taskDao().insertTask(Task(cheduleId =0,startTime ="12:00", endTime ="17:00", content ="[UI/UX] 와이어프레임 작성", status = "complete"))
                taskDB.taskDao().insertTask(Task(cheduleId =0,startTime ="12:00", endTime ="15:30", content ="[회계원리] 레포트 과제 (1)", status = "expected"))
                taskDB.taskDao().insertTask(Task(cheduleId =0,startTime ="12:00", endTime ="14:00", content ="[UI/UX] 와이어프레임 작성", status = "complete"))
                taskDB.taskDao().insertTask(Task(cheduleId =0,startTime ="12:00", endTime ="14:00", content ="[자료구조] 강의 정리",  status ="expected"))
            }
            withContext(Dispatchers.Main) {
                taskDatas.addAll(taskDB.taskDao().getTask())

                connectComposeView()
                clickEvent()
            }
        }

        dataBind()
        var motive = ""
        lateinit var taskList: List<Task>
        lifecycleScope.launch {
            val service = HomepageService(requireContext())
            val response = service.getHomepageData()

            response?.result?.let { result ->
                binding.username = result.user.userName
                motive = result.motivationalMessage
                taskList = result.task
            } ?: run{
                Log.e("Homepage에러", "ㅠㅠ")
            }
        }

        binding.motivationTxt.text = motive

        // 명언 더미데이터 + viewPager연결
        /*val dummyData = listOf("이러쿵", "저러쿵", "화이팅~")

        // viewPager + dotsIndicator연결
        val VPAdapter = TextVPAdapter(motive)
        binding.helloViewpager.adapter = VPAdapter
        binding.dotsIndicator.attachTo(binding.helloViewpager)*/

    }
    // 버튼 클릭 이벤트
    fun clickEvent(){

        binding.homepageCompleteBtn.setOnClickListener {
            val list = taskDatas.filter { it.status == "complete" }
            binding.homepageTodayTask.setContent {
                todayTaskRV(datas = list)
            }
        }
        binding.homepageFailBtn.setOnClickListener {
            val list = taskDatas.filter { it.status == "fail" }
            binding.homepageTodayTask.setContent {
                todayTaskRV(datas = list)
            }
        }
        binding.homepageExpectedBtn.setOnClickListener {
            val list = taskDatas.filter{ it.status == "expected"}
            binding.homepageTodayTask.setContent {
                todayTaskRV(datas = list)
            }
        }
        binding.taskDeleteBtn.setOnClickListener {
            var deletedList = mutableStateListOf<Int>()
            binding.taskDeleteBtn.visibility = INVISIBLE
            binding.taskDeleteCompleteBtn.visibility = VISIBLE
            binding.homepageTodayTask.setContent {
                deleteTask(datas = taskDatas, checkedTask = deletedList)
            }
            binding.taskDeleteCompleteBtn.setOnClickListener {
                lifecycleScope.launch(Dispatchers.IO) {
                    deletedList.forEach { id ->
                        taskDB.taskDao().deleteTaskById(id)
                    }
                    taskDatas = taskDB.taskDao().getTask() as ArrayList<Task>
                    deletedList.clear()
                }

                lifecycleScope.launch(Dispatchers.Main) {
                    binding.homepageTodayTask.setContent {
                        todayTaskRV(datas = taskDatas)
                    }
                }

                binding.taskDeleteBtn.visibility = VISIBLE
                binding.taskDeleteCompleteBtn.visibility = INVISIBLE
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


            todayTaskRV(datas = taskDatas)
        }
    }
    // 데이터 바인딩 연결
    fun dataBind(){
        binding.username = "김가영"
        binding.taskCount = 5
    }
}