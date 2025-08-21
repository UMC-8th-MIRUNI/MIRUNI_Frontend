package com.example.miruni.ui.tool

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.replace
import com.example.miruni.R
import com.example.miruni.databinding.FragmentBlockStartBinding
import com.example.miruni.ui.calendar.ScheduleExecutionFragment

class BlockStartFragment: Fragment() {

    val binding by lazy {
        FragmentBlockStartBinding.inflate(layoutInflater)
    }
    private var aiPlanId = -1         // -1이면 도구에서 온 거 !=면 홈페이지에서 온 거

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }
    private var timer = 0L
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        (activity?.findViewById<View>(R.id.main_top_bar))?.visibility = View.GONE
        (activity?.findViewById<View>(R.id.main_nav))?.visibility = View.GONE

        binding.toolTimeSetting.setOnClickListener {
            /* 시간 설정 */
            binding.toolTimePicker.visibility = View.VISIBLE
            timeSetting()
        }
        aiPlanId = arguments?.getInt("aiPlanId") ?: -1

        initClickListener()

    }
    /* 시간 설정하고 반환받기 */
    private fun timeSetting(){
        setNumberPickerDividerColor(binding.hourPicker, R.color.selectColor)
        setNumberPickerDividerColor(binding.minPicker, R.color.selectColor)
        //setNumberPickerDividerColor(binding.secPicker, R.color.selectColor)

        binding.hourPicker.apply {
            /* 시간 설정 */
            minValue = 0
            maxValue = 23
        }

        binding.minPicker.apply {
            /* 분 설정 */
            minValue = 0
            maxValue = 59
        }

        /*binding.secPicker.apply {
            *//* 초 설정 *//*
            minValue = 0
            maxValue = 59
        }*/

        binding.timeOk.setOnClickListener {
            binding.hour.text = binding.hourPicker.value.toString()
            binding.min.text =  binding.minPicker.value.toString()
            //binding.sec.text =  binding.secPicker.value.toString()

            val h = binding.hourPicker.value
            val m = binding.minPicker.value
            //val s = binding.secPicker.value

            timer = (h*3600L) + (m*60L) //+ s

            binding.toolTimePicker.visibility = View.GONE
        }
    }

    /* numberPicker 커스텀 */
    fun setNumberPickerDividerColor(picker: NumberPicker, color: Int) {
        try {
            val pickerFields = NumberPicker::class.java.declaredFields
            for (field in pickerFields) {
                if (field.name == "mSelectionDivider") {
                    field.isAccessible = true
                    val colorDrawable = ColorDrawable(color)
                    field.set(picker, colorDrawable)
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initClickListener(){
        /* 시작하기 버튼 -> 진행 중 화면으로 이동 + planId 필요 */
        binding.checkStart.setOnClickListener {
            if(timer == 0L) { Toast.makeText(requireContext(), "시간을 선택해주세요", Toast.LENGTH_SHORT).show() }
            else{
                val spf = requireContext().getSharedPreferences("executedTask", AppCompatActivity.MODE_PRIVATE)
                val editor = spf.edit()
                editor.putInt("taskId", aiPlanId)
                editor.apply()

                Log.d("이동하는 id 확인: ", "BlockStartFragment: ${aiPlanId}")

                val fragment = ScheduleExecutionFragment()
                val bundle = Bundle()
                bundle.putLong("timer",timer)
                bundle.putBoolean("blockCheck", arguments?.getBoolean("blockCheck")?: true)
                fragment.arguments = bundle
                requireActivity().supportFragmentManager.beginTransaction().apply {
                    replace(R.id.main_frm, fragment)
                    commit()
                }
            }

        }
        /* 뒤로가기 */
        binding.toolBackClose1.blockBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        /* 닫기 */
        binding.toolBackClose1.blockClose.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.main_frm, ToolFragment())
                commit()
            }
        }
    }
}