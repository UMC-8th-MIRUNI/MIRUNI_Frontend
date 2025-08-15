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
import com.example.miruni.TextVPAdapter
import com.example.miruni.TimetableFragment
import com.example.miruni.TokenManager
import com.example.miruni.api.model.TaskItem
import com.example.miruni.api.model.Tasks
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.data.Task
import com.example.miruni.data.repository.HomepageRepository
import com.example.miruni.databinding.FragmentHomepageBinding
import com.example.miruni.databinding.LayoutCheckpopupBinding
import com.example.miruni.ui.memoir.MemoirCompleteFragment
import com.example.miruni.ui.memoir.MemoirNotFragment
import com.example.miruni.ui.memoir.MemoirWriteFragment

class HomepageFragment: Fragment() {
    private lateinit var binding: FragmentHomepageBinding
    private var tasks = arrayListOf<Tasks>()
    private var allTask = arrayListOf<TaskItem>()
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

        val token = String.format("Bearer ${TokenManager.getToken(requireContext())}")
        val repository = HomepageRepository()
        val factory = HomepageViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HomepageViewModel::class.java]
        viewModel.loadHomepage(token)


        viewModel.homepagedatas.observe(viewLifecycleOwner) { datas ->
            /* 홈페이지 정보 조회 API 연결 */
            val data = datas.result
            binding.apply {
                username = data?.name   // username
                taskCount = "${data?.totalCount}"   // 오늘 남은 할 일
                scheduledCount.text = data?.scheduledCount.toString()   // 예정 개수
                pausedCount.text = data?.pausedCount.toString() // 중지 개수
                completedCount.text = data?.completedCount.toString()   // 완료 개수
                achievement = data?.achievementRate.toString()  // 성취도

                /* 다가오는 다음 일정 */
                if (data?.nextTask != null) {
                    for(next in data?.nextTask!!){
                        nextTaskTitle.text = next.title
                        nextTaskTime.text = "${next.startDate} ${next.startTime}"
                        nextTaskDescription.text = next.description
                    }
                }else{
                    binding.taskPlay.visibility = View.GONE
                }
                /* 오늘의 일정 목록 */
                val tasks = data?.tasks
                /* 모든 일정 다 더하기 */
                allTask = (data?.tasks?.paused!! + data?.tasks?.finished!! + data?.tasks?.notStarted!!) as ArrayList


                if (tasks != null) {
                    clickEvent(tasks)
                }
            }



        }

        // 오늘의 일정 adapter연결
        val layoutManager = GridLayoutManager(requireContext(), 5, GridLayoutManager.HORIZONTAL, false)
        // 모든 일정 다 합치기
        adapter = HomepageRVAdapter(allTask){ id ->
            /* TimetableFragment로 planId 넘겨서 이동 */
            val budle = Bundle()
            budle.putInt("planId", id)
            val fragment = TimetableFragment()
            moveFragment(fragment)
        }

        binding.homepageRecyclerView.layoutManager = layoutManager
        binding.homepageRecyclerView.adapter = adapter

        adapter.setOnClickListener(object : HomepageRVAdapter.onplayClickListener {
            override fun onPlayClick(planId: Int) {
                // 실행 중 화면으로 이동
                TODO("Not yet implemented")
            }

            override fun onMemoirClick(reviewId: Int) {
                var fragment: Fragment
                val bundle = Bundle()
                if(reviewId == 0){
                    // 회고 작성 페이지로 이동
                    fragment = MemoirNotFragment()
                }
                else{
                    // 회고 완료 페이지로 이동
                    fragment = MemoirCompleteFragment()
                    bundle.putInt("reviewId", reviewId)
                }
                fragment.arguments = bundle
                moveFragment(fragment)
            }
        })

    }


    /* 프레그먼트 이동 함수 */
    private fun moveFragment(fragment: Fragment){
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_frm, fragment)
            addToBackStack(null)
            commit()
        }
    }
    private fun clickEvent(tasks: Tasks) {
        // 중지버튼 눌렀을 때
        binding.homepageFailBtn.setOnClickListener {
            adapter = HomepageRVAdapter(tasks.paused){

            }
        }
        // 완료버튼 눌렀을 때
        binding.homepageCompleteBtn.setOnClickListener {
            adapter = HomepageRVAdapter(tasks.finished){

            }
        }
        // 예정 버튼 눌렀을 때
        binding.homepageExpectedBtn.setOnClickListener {
            adapter = HomepageRVAdapter(tasks.notStarted){

            }
        }
        // 전체 버튼 눌렀을 때
        binding.homepageAllBtn.setOnClickListener {
            adapter = HomepageRVAdapter(allTask){ id ->
                /* TimetableFragment로 planId 넘겨서 이동 */
                val budle = Bundle()
                budle.putInt("planId", id)
                val fragment = TimetableFragment()
                moveFragment(fragment)
            }
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
                for(task in deleteTaskId){
                    // rv안에서만 안보이게
                }

                dialog.hide()
                adapter.deleteItem(false)
            }

        }
    }



}


