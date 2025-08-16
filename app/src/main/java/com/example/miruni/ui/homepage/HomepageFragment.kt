package com.example.miruni.ui.homepage

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.miruni.MainActivity
import com.example.miruni.R
import com.example.miruni.TimetableFragment
import com.example.miruni.TokenManager
import com.example.miruni.api.model.DeleteTaskRequest
import com.example.miruni.api.model.TaskItem
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.data.WiseSaying
import com.example.miruni.data.repository.HomepageRepository
import com.example.miruni.databinding.FragmentHomepageBinding
import com.example.miruni.databinding.LayoutCheckpopupBinding
import com.example.miruni.ui.memoir.MemoirCompleteFragment
import com.example.miruni.ui.memoir.MemoirNotFragment
import kotlin.random.Random

class HomepageFragment: Fragment() {
    private lateinit var binding: FragmentHomepageBinding
    private var allTask = arrayListOf<TaskItem>()
    private lateinit var db: ScheduleDatabase
    private lateinit var adapter: HomepageRVAdapter
    private lateinit var viewModel: HomepageViewModel

    private var deleteTaskId: List<Int> = emptyList()

    private lateinit var finished: List<TaskItem> // 완료 일정
    private lateinit var paused: List<TaskItem> // 중지 일정
    private lateinit var notStarted: List<TaskItem> // 예정 일정

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomepageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.setTopBarColor(R.color.main)   // 상단바 색상 변경

        clickEvent()
        connectAdapter()    // 홈페이지 정보 받아서 연결
        wiseSaying()    // 뷰 페이저에 명언 연결
    }


    // 홈페이지 데이터 매개변수로 받아오기
    private fun connectAdapter() {
        val token = String.format("Bearer ${TokenManager.getToken(requireContext())}")
        val repository = HomepageRepository()
        val factory = HomepageViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HomepageViewModel::class.java]
        viewModel.loadHomepage(token)


        viewModel.homepagedatas.observe(viewLifecycleOwner) { data ->
            /* 홈페이지 정보 조회 API 연결 */
            binding.apply {
                username = data.username  // username
                taskCount = "${data.totalCount}"  // 오늘 남은 할 일
                scheduledCount.text = data.scheduledCount.toString()   // 예정 개수
                pausedCount.text = data.pausedCount.toString() // 중지 개수
                completedCount.text = data.completedCount.toString()   // 완료 개수
                achievement = data.achievementRate.toString()  // 성취도

                /* 다가오는 다음 일정 */
                for(date in data.nextTask){
                    nextTaskTitle.text = date.title
                    nextTaskTime.text = "${date.startDate} ${date.startTime}"
                    nextTaskDescription.text = date.description
                }

                /* 오늘의 일정 목록 */
                finished = data.finished
                paused = data.paused
                notStarted = data.notStarted

                /* 모든 일정 다 더하기 */
                allTask = data.allTask as ArrayList<TaskItem>

            }
            clickEvent()   // 오늘의 일정 정렬

            adapter.updateData(allTask) // 데이터 RV에 전달
        }

        /* 오늘의 일정 adapter 연결 */
        val layoutManager = GridLayoutManager(requireContext(), 5, GridLayoutManager.HORIZONTAL, false)
        adapter = HomepageRVAdapter(){ id ->
            /* TimetableFragment로 planId 넘겨서 이동 */
            moveTimetableFragment(id)
        }

        binding.homepageRecyclerView.layoutManager = layoutManager
        binding.homepageRecyclerView.adapter = adapter

        adapter.setOnClickListener(object : HomepageRVAdapter.onplayClickListener {
            override fun onPlayClick(planId: Int) {
                /* 실행 중 화면으로 이동 */
                TODO("Not yet implemented")
            }

            override fun onMemoirClick(reviewId: Int, planId: Int) {
                /* 회고 페이지 이동 */
                moveMemoirFragment(reviewId, planId)
            }

            override fun ondeleteTask(request: DeleteTaskRequest) {
                /* 삭제*/

            }
        })

    }
    /* MemoirFraagment 이동 함수 */
    private fun moveMemoirFragment(reviewId: Int, planId: Int){
        var fragment: Fragment
        val bundle = Bundle()

        if(reviewId == 0){  // 회고 미작성 페이지로 이동
            fragment = MemoirNotFragment()
            bundle.putInt("planId", planId)
        }
        else{   // 회고 완료 페이지로 이동
            fragment = MemoirCompleteFragment()
            bundle.putInt("reviewId", reviewId)
        }

        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_frm, fragment)
            addToBackStack(null)
            arguments = bundle
            commit()
        }

    }

    /* TimetableFragment 이동 함수 */
    private fun moveTimetableFragment(planId: Int){
        val fragment = TimetableFragment()
        val bundle = Bundle()
        bundle.putInt("planId", planId)

        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_frm, fragment)
            addToBackStack(null)
            arguments = bundle
            commit()
        }

    }
    private fun clickEvent() {
        // 중지버튼 눌렀을 때
        binding.homepageFailBtn.setOnClickListener {
            adapter.updateData(paused)
        }
        // 완료버튼 눌렀을 때
        binding.homepageCompleteBtn.setOnClickListener {
            adapter.updateData(finished)
        }
        // 예정 버튼 눌렀을 때
        binding.homepageExpectedBtn.setOnClickListener {
            adapter.updateData(notStarted)
        }
        // 전체 버튼 눌렀을 때
        binding.homepageAllBtn.setOnClickListener {
            adapter.updateData(allTask)
        }

        // 다가오는 일정 play 실행
        binding.taskPlay.setOnClickListener {
            /* 실행 중 화면으로 이동 */
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

            // 클릭 한 리스트 반환
            val list = adapter.getSelectedItems()

            val token = String.format("Bearer ${TokenManager.getToken(requireContext())}")

            /* 내부 저장해서 보내기..*/
            for(data in list){
                viewModel.deleteTask(token, data)
            }
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
                for(task in deleteTaskId){
                    // rv안에서만 안보이게
                }

                dialog.hide()
                adapter.deleteItem(false)
            }

        }
    }

    private fun wiseSaying(){
        val wise = WiseSaying()
        val wiseSaying = mutableListOf<String>()

        repeat(3){
            wiseSaying.add(wise.list.get(Random.nextInt(0,wise.list.size-1)))
        }

        val VPAdapter = TextVPAdapter(wiseSaying)
        binding.helloViewpager.adapter = VPAdapter
        binding.dotsIndicator.attachTo(binding.helloViewpager)
    }


}


