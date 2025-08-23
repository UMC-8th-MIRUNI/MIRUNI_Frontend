package com.example.miruni

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class ReportOpenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_report_open, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val pieChart = view.findViewById<PieChart>(R.id.pieChart)

        // PieChart 기본 설정
        pieChart.apply {
            setUsePercentValues(true)       // 퍼센트 표시
            description.isEnabled = false   // 오른쪽 하단 description 제거
            isDrawHoleEnabled = true        // 가운데 Hole
            setHoleColor(Color.TRANSPARENT) // 홀 색 투명
            setEntryLabelColor(Color.BLACK) // 항목 텍스트 색
            legend.isEnabled = true         // 범례 활성화
            setNoDataText("데이터가 없습니다") // 데이터 없을 때 메시지
        }

        // 데이터 준비
        val entries = ArrayList<PieEntry>().apply {
            add(PieEntry(40f, "A"))
            add(PieEntry(30f, "B"))
            add(PieEntry(20f, "C"))
            add(PieEntry(10f, "D"))
        }

        // DataSet 생성
        val dataSet = PieDataSet(entries, "카테고리").apply {
            colors = listOf(Color.RED, Color.BLUE, Color.GREEN, Color.MAGENTA) // 색상 명확히
            valueTextSize = 12f
            valueTextColor = Color.WHITE
        }

        // Data 객체 생성
        val data = PieData(dataSet)

        // 차트에 데이터 적용
        pieChart.data = data
        pieChart.invalidate() // 꼭 invalidate() 호출
        pieChart.animateY(1000)
    }
}