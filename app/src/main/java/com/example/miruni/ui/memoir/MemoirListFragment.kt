package com.example.miruni.ui.memoir

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CalendarView
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
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
import com.example.miruni.data.repository.MemoirRepository
import com.example.miruni.databinding.FragmentMemoirListBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.time.Instant
import java.time.Instant.*
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

private var body: List<ReviewDate> = emptyList()
private lateinit var token: String
private lateinit var viewModel: MemoirViewModel
private val repository = MemoirRepository()
private val factory = MemoirViewModelFactory(repository)

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

        viewModel = ViewModelProvider(this, factory)[MemoirViewModel::class.java]

        memoirCountByDate() // 날짜별 회고록 갯수 조회

        searchCalender()    // 날짜 검색

        // 뒤로 가기
        binding.listBack.setOnClickListener { requireActivity().supportFragmentManager.popBackStack() }
    }

    /* "yyyy-mm-dd" 형식으로 회고 날짜 검색 API에 넘기기 */
    private fun searchCalender(){
        var format : String = ""
        binding.dateClick.setOnClickListener {
            // PopupWindow 레이아웃 inflate
            val calendarView = CalendarView(requireContext())

            val popup = PopupWindow(calendarView,
                binding.dateClick.width, ViewGroup.LayoutParams.WRAP_CONTENT, true)
            popup.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_custom_calendar))
            popup.isOutsideTouchable = true


            // 날짜 선택 이벤트
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                format = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                binding.dateClick.setText(format)
                popup.dismiss()

                memoirSearch(format)
            }
            // Popup 보여주기
            popup.showAsDropDown(binding.dateClick)


        }
    }

    /* 날짜별 회고록 갯수 조회 */
    private fun memoirCountByDate(){
        token = String.format("Bearer ${TokenManager.getToken(requireContext())}")
        viewModel.memoirCountByDate(token)

        viewModel.countDate.observe(viewLifecycleOwner) { data ->
            body = data?.result ?: emptyList()
            setRecyclerView(body)
        }
    }

    /* 회고 날짜 검색 조회 */
    private fun memoirSearch(query: String){
        viewModel.memoirSearch(token, query)
        viewModel.searchData.observe(viewLifecycleOwner){ data ->
            val review = listOf( data?.result ?: ReviewDate("", 0) )
            setRecyclerView(review)
        }
    }

    /* 리사이클러뷰 이동 메서드 */
    private fun setRecyclerView(data: List<ReviewDate>) {
            val memoirAdapter = MemoirListRVAdapter { date ->
                // MemoirAddFragment로 이동
                val fragment = MemoirAddFragment()

                val bundle = Bundle()
                bundle.putString("date", date)
                fragment.arguments = bundle


                requireActivity().supportFragmentManager.beginTransaction().apply {
                    replace(R.id.main_frm, fragment)
                    addToBackStack(null)
                    Log.d("보내는 날짜", "${arguments}")
                    commit()
                }

            }
            val spacer = resources.getDimensionPixelSize(R.dimen.recycler_dimen)

            binding.memoirRV.apply {
                adapter = memoirAdapter
                layoutManager = GridLayoutManager(requireContext(), 2)
                if (itemDecorationCount == 0)
                    addItemDecoration(RVSpacer(spacer))
            }

            memoirAdapter.initRecyclerView(data)
    }
}
