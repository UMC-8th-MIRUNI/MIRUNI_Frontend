package com.example.miruni.ui.calendar

import android.app.DatePickerDialog
import android.graphics.Color
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import com.example.miruni.MainActivity
import com.example.miruni.R
import com.example.miruni.api.ApiService
import com.example.miruni.api.RegistrationScheduleResponse
import com.example.miruni.api.ScheduleToRegister
import com.example.miruni.api.getRetrofit
import com.example.miruni.databinding.FragmentRegistrationScheduleBinding
import com.example.miruni.databinding.LayoutDropdownPriorityBinding
import com.example.miruni.databinding.LayoutDropdownScheduleTypeBinding
import com.example.miruni.databinding.LayoutPopupSplitDetailGuideBinding
import com.example.miruni.databinding.LayoutPopupSplitGuideBinding
import com.example.miruni.databinding.LayoutScheduleSplitBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class RegistrationScheduleFragment : Fragment() {
    private lateinit var binding: FragmentRegistrationScheduleBinding

    private var selectedDate = ""
    private var isSelectedExctDate = 0

    private lateinit var priorityDropdown: PopupWindow
    private val priorityItems = arrayListOf("상", "중", "하")
    private var selectedPriority = ""
    private var selectedScheduleType = -1

    private val scheduleTypeList = listOf(
        "몰입형/사고 중심 작업",
        "창작/표현 작업",
        "학습/정보 정리 작업",
        "실무/행정 처리 작업",
        "반복/루틴형 작업",
        "협업/의사소통 작업",
        "준비/계획형 작업"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrationScheduleBinding.inflate(layoutInflater, container, false)

        hideNavigationBar()
        initRegistrationClickListener()
        initSplitClickListener()

        return binding.root
    }

    /**
     * 네비게이션바 숨기기
     */
    private fun hideNavigationBar() {
        val activity = requireActivity() as MainActivity
        val navigationBar = activity.findViewById<ConstraintLayout>(R.id.main_nav)
        val homeBtn = activity.findViewById<ImageView>(R.id.nav_home_iv)

        navigationBar.visibility = View.GONE
        homeBtn.visibility = View.GONE
        binding.scheduleRegistrationInclude.root.visibility = View.VISIBLE
    }

    /**
     * '일정 등록하기' 화면 클릭 이벤트
     */
    private fun initRegistrationClickListener() {
        binding.scheduleRegistrationInclude.scheduleRegistrationIncludeTopbar.apply {
            /** 뒤로 가기 */
            scheduleRegistrationTopbarBackIv.setOnClickListener {
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, CalendarFragment())
                    .commitAllowingStateLoss()
            }
            /** x 버튼 */
            scheduleRegistrationTopbarCancelIv.setOnClickListener {
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, CalendarFragment())
                    .commitAllowingStateLoss()
            }
        }

        binding.scheduleRegistrationInclude.scheduleRegistrationIncludeContent.apply {
            /** 마감기한 설정 */
            scheduleRegistrationContentDeadlineIv.setOnClickListener {
                showDatePickerDialog(scheduleRegistrationContentDeadlineTv, 0)
                isSelectedExctDate = 0
            }
            /** 일정 수행 날짜 설정 */
            scheduleRegistrationContentDateIv.setOnClickListener {
                showDatePickerDialog(scheduleRegistrationContentDateTv, 1)
                isSelectedExctDate = 1
            }
            /** 우선 순위 설정 */
            scheduleRegistrationContentPriorityTv.setOnClickListener {
                showPriorityDropdown(scheduleRegistrationContentPriorityTv)
            }
        }

        binding.scheduleRegistrationInclude.scheduleRegistrationIncludeBtn.apply {
            /** 등록하기 */
            scheduleRegistrationContentBtnRegister.setOnClickListener {
                val scheduleToRegister = setRequestSchedule()

                if (scheduleToRegister == null) return@setOnClickListener

                val registerService = getRetrofit().create(ApiService::class.java)
                registerService.registrationSchedule(scheduleToRegister).enqueue(object : Callback<RegistrationScheduleResponse> {
                    /**
                     * 응답이 왔을 때
                     */
                    override fun onResponse(
                        call: Call<RegistrationScheduleResponse>,
                        response: Response<RegistrationScheduleResponse>
                    ) {
                        Log.d("RegistrationSchedule/Register/SUCCESS", response.toString())

                        val resp: RegistrationScheduleResponse = response.body()!!
                        val planId = resp.planId
                        val title = resp.title
                        val deadline = resp.deadline
                        val isDone = resp.isDone
                        Log.d("RegistrationSchedule/Register/Response", "planId=${planId}, title=${title}, deadline=$${deadline}, isDone=${isDone}")

                        (context as MainActivity).supportFragmentManager.beginTransaction()
                            .replace(R.id.main_frm, CalendarFragment())
                            .commitAllowingStateLoss()
                    }

                    /**
                     * 네트워크 연결 자체가 실패했을 때
                     */
                    override fun onFailure(call: Call<RegistrationScheduleResponse>, t: Throwable) {
                        Log.d("RegisterSchedule/FAILURE", t.message.toString())
                    }

                })
            }
            /** 쪼개기 */
            scheduleRegistrationContentBtnSplit.setOnClickListener {
                binding.scheduleRegistrationInclude.root.visibility = View.GONE
                binding.scheduleSplitInclude.root.visibility = View.VISIBLE
                binding.scheduleSplitInclude.scheduleSplitIncludeTopbar.scheduleRegistrationTopbarTitleTv.text = "쪼개기"
            }
        }
    }

    /**
     * '쪼개기' 화면 클릭 이벤트
     */
    private fun initSplitClickListener() {
        binding.scheduleSplitInclude.scheduleSplitIncludeTopbar.apply {
            /** 뒤로 가기 */
            scheduleRegistrationTopbarBackIv.setOnClickListener {
                binding.scheduleRegistrationInclude.root.visibility = View.VISIBLE
                binding.scheduleSplitInclude.root.visibility = View.GONE
                binding.scheduleSplitInclude.scheduleSplitIncludeTopbar.scheduleRegistrationTopbarTitleTv.text = "일정 등록하기"
            }
            /** x표시 */
            scheduleRegistrationTopbarCancelIv.setOnClickListener {
                (context as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frm, CalendarFragment())
                    .commitAllowingStateLoss()
            }
        }
        binding.scheduleSplitInclude.apply {
            /** 작업 유형 선택 드롭다운 */
            scheduleSplitTypeFrm.setOnClickListener {
                scheduleSplitTypeMoreIv.visibility = View.GONE
                showScheduleTypeDropdown(it)
            }
            /** 쪼개기 가이드 드롭다운 */
            scheduleSplitSplitGuideIv.setOnClickListener {
                initSplitGuidePopup(it)
            }
            /** 쪼개기 */
            scheduleSplitSplitBtn.setOnClickListener {
                TODO()
            }
        }
    }

    /**
     * 날짜 선택
     */
    private fun showDatePickerDialog(anchor: TextView, selectType: Int) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        context?.let { it1 ->
            DatePickerDialog(it1, { _, year, month, day ->
                run {
                    // / date -> deadline -> 반영할 필요 없음
                    if (selectType < 1) { // x -> deadline / x -> date / deadline -> deadline / deadline -> date
                        selectedDate = String.format("${year}-${DecimalFormat("00").format(month+1)}-${DecimalFormat("00").format(day)}")
                    } else if (isSelectedExctDate == 1 && selectType == 1) { // date -> date
                        selectedDate = String.format("${year}-${DecimalFormat("00").format(month+1)}-${DecimalFormat("00").format(day)}")
                    }
                    anchor.text = String.format("${year}.${DecimalFormat("00").format(month+1)}.${DecimalFormat("00").format(day)}")
                }
            }, year, month, day)
        }?.show()
    }

    /**
     * 우선 순위 드롭다운 설정
     */
    private fun initPriorityDropdown(
        anchor: View,
        items: List<String>,
        selectedItem: String?,
        onItemSelected: (String) -> Unit) {

        anchor.setBackgroundResource(R.drawable.bg_selected_priority_dropdown)

        val dropdownView = LayoutDropdownPriorityBinding.inflate(layoutInflater)
        priorityDropdown = PopupWindow(
            dropdownView.root,
            anchor.width,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        priorityDropdown.elevation = 5f
        priorityDropdown.setBackgroundDrawable(Color.WHITE.toDrawable())
        priorityDropdown.isOutsideTouchable = true

        val priorityList = listOf(
            dropdownView.dropdownPriorityTop,
            dropdownView.dropdownPriorityMid,
            dropdownView.dropdownPriorityBtm
        )

        items.zip(priorityList).forEach { (text, textView) ->
            textView.text = text
            textView.setOnClickListener {
                onItemSelected(text)
                anchor.setBackgroundResource(R.drawable.bg_ababab_square_7)
                priorityDropdown.dismiss()
            }

            if (text == selectedItem) {
                textView.setTextColor(Color.WHITE)
                textView.setBackgroundColor("#1AE019".toColorInt())
            } else {
                textView.setTextColor(Color.BLACK)
                Color.TRANSPARENT
            }
        }

        priorityDropdown.showAsDropDown(anchor, 0, -5)
    }

    /**
     * 쪼개기 가이드 팝업 설명
     */
    private fun initSplitGuidePopup(anchor: View) {
        val dropdownView = LayoutPopupSplitGuideBinding.inflate(layoutInflater)
        val splitGuidePopup = PopupWindow(
            dropdownView.root,
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            true
        )

        splitGuidePopup.elevation = 5f
        splitGuidePopup.isOutsideTouchable = true

        /** 상세 설명 보기 */
        dropdownView.apply {
            popupSplitGuideDetailTv.setOnClickListener {
                splitGuidePopup.dismiss()
                initSplitDetailGuidePopup(anchor)
            }
            popupSplitGuideCloseIv.setOnClickListener {
                splitGuidePopup.dismiss()
            }
        }
        splitGuidePopup.showAsDropDown(anchor, -10, -10)
    }

    /**
     * 쪼개기-일정 유형 선택 상세 설명 보기
     */
    private fun initSplitDetailGuidePopup(anchor: View) {
        val dropdownView = LayoutPopupSplitDetailGuideBinding.inflate(layoutInflater)
        val splitDetailGuidePopup = PopupWindow(
            dropdownView.root,
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT,
            true
        )

        splitDetailGuidePopup.elevation = 5f
        splitDetailGuidePopup.isOutsideTouchable = true

        /** 상세 설명 보기 */
        dropdownView.apply {
            popupSplitDetailGuideCloseIv.setOnClickListener {
                splitDetailGuidePopup.dismiss()
            }
        }
        splitDetailGuidePopup.showAsDropDown(anchor, -10, -10)
    }

    /**
     * 일정 유형 선택 드롭다운 설정
     */
    private fun initScheduleTypeDropdown(
        anchor: View,
        onItemSelected: (Int) -> Unit) {

        anchor.setBackgroundResource(R.drawable.bg_selected_priority_dropdown)

        val dropdownView = LayoutDropdownScheduleTypeBinding.inflate(layoutInflater)
        val typeDropdown = PopupWindow(
            dropdownView.root,
            anchor.width,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )
        typeDropdown.elevation = 5f
        typeDropdown.setBackgroundDrawable(Color.WHITE.toDrawable())
        typeDropdown.isOutsideTouchable = true

        val typeViewList = listOf(
            dropdownView.dropdownScheduleType1,
            dropdownView.dropdownScheduleType2,
            dropdownView.dropdownScheduleType3,
            dropdownView.dropdownScheduleType4,
            dropdownView.dropdownScheduleType5,
            dropdownView.dropdownScheduleType6,
            dropdownView.dropdownScheduleType7
            )

        val viewList = listOf(
            Triple(dropdownView.dropdownScheduleType1Rank, dropdownView.dropdownScheduleType1Iv, dropdownView.dropdownScheduleType1Tv),
            Triple(dropdownView.dropdownScheduleType2Rank, dropdownView.dropdownScheduleType2Iv, dropdownView.dropdownScheduleType2Tv),
            Triple(dropdownView.dropdownScheduleType3Rank, dropdownView.dropdownScheduleType3Iv, dropdownView.dropdownScheduleType3Tv),
            Triple(dropdownView.dropdownScheduleType4Rank, dropdownView.dropdownScheduleType4Iv, dropdownView.dropdownScheduleType4Tv),
            Triple(dropdownView.dropdownScheduleType5Rank, dropdownView.dropdownScheduleType5Iv, dropdownView.dropdownScheduleType5Tv),
            Triple(dropdownView.dropdownScheduleType6Rank, dropdownView.dropdownScheduleType6Iv, dropdownView.dropdownScheduleType6Tv),
            Triple(dropdownView.dropdownScheduleType7Rank, dropdownView.dropdownScheduleType7Iv, dropdownView.dropdownScheduleType7Tv),
        )

        typeViewList.forEachIndexed() { index, type  ->
            type.setOnClickListener {
                onItemSelected(index)
                typeDropdown.dismiss()
                anchor.setBackgroundResource(R.drawable.bg_ababab_square_7)
            }

            if (index == selectedScheduleType) {
                viewList[index].first.setTextColor(Color.WHITE)
                viewList[index].second.setColorFilter(Color.WHITE)
                viewList[index].first.setTextColor(Color.WHITE)

                type.setBackgroundColor("#1AE019".toColorInt())
            } else {
                viewList[index].first.setTextColor("#666666".toColorInt())
                viewList[index].second.setColorFilter("#666666".toColorInt())
                viewList[index].first.setTextColor("#666666".toColorInt())
                Color.TRANSPARENT
            }
        }

        typeDropdown.showAsDropDown(anchor, 0, -5)

    }


    /**
     * 우선 순위 드롭다운 결과 처리
     */
    private fun showPriorityDropdown(anchor: View) {
        initPriorityDropdown(anchor, priorityItems, selectedPriority) { selected ->
            selectedPriority = selected
            binding.scheduleRegistrationInclude.scheduleRegistrationIncludeContent.scheduleRegistrationContentPriorityTv.text = selected
        }
    }

    /**
     * 일정 유형 선택 드롭다운 결과 처리
     */
    private fun showScheduleTypeDropdown(anchor: View) {
        initScheduleTypeDropdown(anchor) {selected ->
            selectedScheduleType = selected
            binding.scheduleSplitInclude.scheduleSplitTypeTv.text = scheduleTypeList[selected]
            binding.scheduleSplitInclude.scheduleSplitTypeMoreIv.visibility = View.VISIBLE
        }
    }

    /**
     * 백엔드로 보낼 Request 바디 생성
     */
    private fun setRequestSchedule(): ScheduleToRegister? {

        val title = binding.scheduleRegistrationInclude.scheduleRegistrationIncludeContent.scheduleRegistrationContentTitleEt.text.toString()
        Log.d("RegistrationSchedule/title", title)
        val deadline = String.format("${selectedDate}T00:00:00.000")
        Log.d("RegistrationSchedule/deadline", deadline)
        val priority = selectedPriority
        Log.d("RegistrationSchedule/priority", priority)
        val description = binding.scheduleRegistrationInclude.scheduleRegistrationIncludeContent.scheduleRegistrationContentCommentEt.text.toString()
        Log.d("RegistrationSchedule/description", description)

        if (title.isEmpty()) {
            Toast.makeText(context as MainActivity, "제목을 입력해주세요", Toast.LENGTH_SHORT).show()
            return null
        }
        if (deadline.isEmpty()) {
            Toast.makeText(context as MainActivity, "마감기한 혹은 수행 날짜를 정해주세요", Toast.LENGTH_SHORT).show()
            return null
        }
        if (priority.isEmpty()) {
            Toast.makeText(context as MainActivity, "우선 순위를 정해주세요", Toast.LENGTH_SHORT).show()
            return null
        }
        if (description.isEmpty()) {
            Toast.makeText(context as MainActivity, "한 줄 설명은 작성하지 않았어요", Toast.LENGTH_SHORT).show()
        }

        return ScheduleToRegister(
            title,
            description,
            deadline
        )
    }
}

enum class ScheduleType {
    IMMERSIVE,
    CREATIVE,
    STUDY_ORGANIZATION,
    PRACTICAL_ADMIN,
    ROUTINE,
    COLLAB_COMMUNICATION,
    PREPARATION_PLANNING
}