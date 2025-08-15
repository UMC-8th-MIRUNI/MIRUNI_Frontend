package com.example.miruni.ui.storage

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.miruni.MainActivity
import com.example.miruni.R
import com.example.miruni.api.StorageApi
import com.example.miruni.data.repository.StorageRepository
import com.example.miruni.databinding.FragmentStorageBinding
import com.example.miruni.ui.homepage.t
import com.example.miruni.ui.memoir.MemoirListFragment
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate

class StorageFragment: Fragment() {

    val binding by lazy {
        FragmentStorageBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: StorageViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    @SuppressLint("ResourceAsColor")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* 상단 topbar 색상 및 View변경 */
        (activity as MainActivity?)?.setTopBarColor(R.color.white)
        (activity?.findViewById<View>(R.id.main_top_bar))?.visibility = View.VISIBLE

        val today = LocalDate.now()
        val year = today.year
        val month = today.monthValue

        val token = "Bearer ${t}"

        val repository = StorageRepository()
        val factory = StorageViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[StorageViewModel::class.java]
        viewModel.loadStorage(token, year, month)

        clickBtn() // 버튼 함수
        showPiechart() // 파이차트

        viewModel.storageData.observe(viewLifecycleOwner){ data ->


            if(data.canOpenThisMonth){
                /* 이번달 리포트 충족 */

                if(data.isOpenButtonVisible){
                    /* false면 '저번달 리포트 보기' 만 보여주기 / true면 '오픈하기' 버튼 */
                    binding.reportActive.visibility = View.VISIBLE
                    binding.reportInactive.visibility = View.GONE
                }else{
                    //binding.reportActive.visibility = View.GONE
                    //binding.reportInactive.visibility = View.VISIBLE
                }

                if(data.isOpenedThisMonth){
                    // 이번달 리포트 오픈 API 조회
                    binding.openText.text = "이번달 리포트"

                    binding.reportLockInactive.visibility = View.GONE
                    binding.reportLockActive.visibility = View.VISIBLE

                    binding.reportOpen.setOnClickListener {
                    // 이번달 리포트 조회 페이지로 이동

                    }
                }
            }else{
                /*binding.reportActive.visibility = View.GONE
                binding.reportInactive.visibility = View.VISIBLE

                binding.reportLockInactive.visibility = View.VISIBLE
                binding.reportLockActive.visibility = View.GONE*/
            }

            if(data.isOpenedThisMonth){
            // 저번달 리포트 조회 페이지로 이동

            }
        }

    }

    /*버튼 함수*/
    fun clickBtn(){

        binding.reportGuide.setOnClickListener {
            // 팝업 레이아웃 띄우기
            popupWindow()
        }

        binding.moveMemoir.setOnClickListener {
            // MemoirListFragment로이동
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.main_frm, MemoirListFragment())
                addToBackStack(null)
                commit()
            }
        }

    }
    /* 리포트 팝업 보여주기*/
    fun popupWindow(){
        val popupView = layoutInflater.inflate(R.layout.layout_popup_report_guide, null)
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true // 외부 클릭 시 닫힘
        )
        popupWindow.isOutsideTouchable = true
        popupWindow.showAsDropDown(binding.reportGuide, -80, 10)

        popupView.setOnClickListener {
            popupWindow.dismiss()   // 팝업 닫기
        }
    }

    /* 파이 차트 구현*/
    fun showPiechart(){
        var percent = 0f
        viewModel.percent.observe(viewLifecycleOwner) { per ->
            percent = per.toFloat()
            binding.openPercent.text = per.toString()

            if (percent <= 0f) percent = 0.1f

            // 파이차트의 퍼센티지 100
            binding.reportChart.setUsePercentValues(true)

            // 데이터 설정
            val chartData = listOf(
                PieEntry(percent, "진행률"),
                PieEntry((100F - percent), "집")
            )

            // 섹션 색상 설정
            val dataSet = PieDataSet(chartData, "")
            dataSet.colors = listOf(
                ContextCompat.getColor(requireActivity(), R.color.main),
                ContextCompat.getColor(requireActivity(), R.color.backgroundColor)
            )

            // pieChart 안에들어간 value값 지우기
            dataSet.setDrawValues(false)

            //슬라이스 간 간격
            dataSet.sliceSpace = 2f
            // 데이터 설정 값 삽입
            val pieData = PieData(dataSet)

                binding.run {
                    reportChart.apply {
                        data = pieData
                        description.isEnabled = false
                        legend.isEnabled = false
                        setDrawEntryLabels(false)   // 차트 위 텍스트 제거
                        // 차트 끝 둥글게
                        //setDrawRoundedSlices(true)
                        holeRadius = 60f    //도넛 홀 조정
                        animate()
                }
            }
        }
    }

}