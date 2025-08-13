package com.example.miruni.ui.memoir

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.miruni.MainActivity
import com.example.miruni.R
import com.example.miruni.databinding.FragmentStorageBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class StorageFragment: Fragment() {

    val binding by lazy {
        FragmentStorageBinding.inflate(layoutInflater)
    }

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

        showPiechart() // 차이차트
        clickBtn() // 버튼 함수

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

        // 파이차트의 퍼센티지 100
        binding.reportChart.setUsePercentValues(true)

        // 데이터 설정
        val chartData = listOf(
            PieEntry(70F, "커피") ,
            PieEntry(30F, "집")
        )

        // 섹션 색상 설정
        val dataSet = PieDataSet(chartData, "")
        dataSet.colors = listOf(
            ContextCompat.getColor(requireActivity(), R.color.main) ,
            //ContextCompat.getColor(requireActivity(),R.color.backgroundColor)
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
                setDrawRoundedSlices(true)


                holeRadius = 60f    //도넛 홀 조정

                animate()
            }
        }
    }

}