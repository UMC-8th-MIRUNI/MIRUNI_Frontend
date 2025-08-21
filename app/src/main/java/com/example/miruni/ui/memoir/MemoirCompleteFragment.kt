package com.example.miruni.ui.memoir

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import com.example.miruni.R
import com.example.miruni.api.ApiService
import com.example.miruni.data.Mood
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.databinding.FragmentMemoirCompleteBinding
import android.graphics.Color
import android.util.Log
import android.widget.EditText
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.marginStart
import androidx.lifecycle.ViewModelProvider
import com.example.miruni.TokenManager
import com.example.miruni.api.model.MemoirDetailResponse
import com.example.miruni.data.repository.MemoirRepository


private var body: MemoirDetailResponse? = null
private var db: ScheduleDatabase? = null
private var reviewId = 0
private lateinit var token: String
private val repository = MemoirRepository()
private val factory = MemoirViewModelFactory(repository)
private lateinit var viewModel: MemoirViewModel
// 단일 회고 상세 조회 api연결
class MemoirCompleteFragment: Fragment() {
    val binding by lazy {
        FragmentMemoirCompleteBinding.inflate(layoutInflater)
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

        (activity?.findViewById<View>(R.id.main_top_bar))?.visibility = View.GONE
        (activity?.findViewById<View>(R.id.main_nav))?.visibility = View.GONE

        // 메뉴 이미지 visible전환
        binding.completeLayout.memoirWriteMenu.visibility = View.VISIBLE
        db = ScheduleDatabase.getInstance(requireContext())

        viewModel = ViewModelProvider(this, factory)[MemoirViewModel::class.java]

        token = String.format("Bearer ${TokenManager.getToken(requireContext())}")
        reviewId = requireArguments().getInt("reviewId")

        viewModel.getMemoirDetail(token, reviewId)
        viewModel.detailData.observe(viewLifecycleOwner){ data ->
            body = data
            /* 데이터 연결 */
            binding.completeLayout.apply {
                memoirTitle.memoirWriteTitle.text = data?.result?.title
                memoirTitle.memoirWriteDate.text = data?.result?.createdAt
                memoirWriteTxt.setText(data?.result?.memo ?: "작성된 메모가 없습니다.")
                memoirTitle.memoirDescription.text = data?.result?.description

                val achievement = data?.result?.achievement ?: 0
                updateView(archievePercent, achievement)    // archievePercent style 수정
            }
            val mood = data?.result?.mood ?: Mood.ANXIOUS

            moodUpdate(mood)
            // 메뉴
            binding.completeLayout.memoirWriteMenu.setOnClickListener {
                showMemu(it, data)
            }
        }

        initClickListener() // 버튼 클릭


    }
    /* 프레그먼트 이동 함수 */
    private fun moveFragment(fragment: Fragment){
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_frm, fragment)
            commit()
        }
    }

    private fun showMemu(view: View, data: MemoirDetailResponse){
        val menu = PopupMenu(requireContext(), view)
        menu.menuInflater.inflate(R.menu.memoir_write_menu, menu.menu)

        menu.setOnMenuItemClickListener { item ->
            when(item.itemId){
                /* 수정하기 */
                R.id.write_modify ->{
                    val bundle = Bundle().apply {
                        val reviewId = data?.result?.id ?: throw IllegalStateException("id = Null입니다")
                        putInt("reviewId", reviewId)
                        putInt("achievement", data?.result?.achievement ?: 0)
                        putString("mood", data?.result?.mood?.name)
                        putString("memo", data?.result?.memo)
                        putString("title", data?.result?.title)
                        putString("createdAt", data?.result?.createdAt)
                        putString("description", data?.result?.description)
                    }
                    val fragment = MemoirModifyFragment()
                    fragment.arguments = bundle

                    // 화면 전환
                    moveFragment(fragment)
                    true
                }
                /* 삭제하기 */
                R.id.write_delete -> {
                    viewModel.getMemoirDelete(token, reviewId)
                    // 화면 전환
                    moveFragment(MemoirListFragment())
                    true
                }
                else -> false
            }
        }
        menu.show()
    }
    private fun moodUpdate(mood: Mood){
        val check = mood.name

        when(check){
            "HAPPY" -> {
                binding.completeLayout.happyMiruniInactive.visibility = View.INVISIBLE
                binding.completeLayout.happyMiruniActive.visibility = View.VISIBLE
            }
            "SAD" -> {
                binding.completeLayout.sadMiruniInactive.visibility = View.INVISIBLE
                binding.completeLayout.sadMiruniActive.visibility = View.VISIBLE
            }
            "ANGRY" -> {
                binding.completeLayout.angryMiruniInactive.visibility = View.INVISIBLE
                binding.completeLayout.angryMiruniActive.visibility = View.VISIBLE
            }
            "RELAXED" -> {
                binding.completeLayout.disappointedMiruniInactive.visibility = View.INVISIBLE
                binding.completeLayout.disappointedMiruniActive.visibility = View.VISIBLE
            }
            else -> {
                binding.completeLayout.surprisedMiruniInactive.visibility = View.INVISIBLE
                binding.completeLayout.surprisedMiruniActive.visibility = View.VISIBLE
            }
        }
    }
    /* 버튼 기능 */
    private fun initClickListener(){
        // 회고 작성 완료 페이지로 이동
        binding.completeLayout.memoirWriteOk.setOnClickListener {
            moveFragment(MemoirListFragment())
        }
        // 뒤로가기
        binding.completeLayout.memoirTitle.memoirWriteBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
    /* 성취도 숫자 스타일 변경 */
    private fun updateView(achievement: EditText, text: Int){
        val font = ResourcesCompat.getFont(context, R.font.dmsans_bold)
        achievement.apply {
            setText(text.toString())
            background = null
            setTextColor(Color.parseColor("#1EC718"))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 30f)
            typeface = font
        }
    }
}
