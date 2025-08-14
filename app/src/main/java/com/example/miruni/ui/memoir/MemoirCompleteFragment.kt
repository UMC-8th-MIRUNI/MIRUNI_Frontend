package com.example.miruni.ui.memoir

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.miruni.R
import com.example.miruni.api.ApiService
import com.example.miruni.api.model.MemoirDetailResponse
import com.example.miruni.api.getRetrofit
import com.example.miruni.data.Mood
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.databinding.FragmentMemoirCompleteBinding
import com.example.miruni.ui.homepage.t
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


private var body: MemoirDetailResponse? = null
private var db: ScheduleDatabase? = null
private lateinit var token: String
private var reviewId = 0
private lateinit var api: ApiService

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

        // 메뉴 이미지 visible전환
        binding.completeLayout.memoirWriteMenu.visibility = View.VISIBLE
        db = ScheduleDatabase.getInstance(requireContext())

        lifecycleScope.launch{
            try{
                token = "Bearer $t"
                api = getRetrofit().create(ApiService::class.java)
                reviewId = requireArguments().getInt("reviewId")
                val response = api.memoirDetail(token, reviewId)
                Log.d("단일 회고 상세 조회", "성공: ${response}")

                if(response.isSuccessful){
                    body = response.body()!!
                    Log.d("단일 회고 상세 조회", "저장 회고 내용: ${body}")

                    binding.completeLayout.apply {
                        memoirWriteTitle.text = body?.result?.title
                        memoirWriteDate.text = body?.result?.createdAt
                        memoirWriteTxt.setText(body?.result?.memo ?: "")
                        memoirDescription.text = body?.result?.description

                        /* archievePercent style 수정*/
                        val font = ResourcesCompat.getFont(context, R.font.dmsans_bold)
                        archievePercent.apply {
                            setText(body?.result?.achievement.toString())
                            background = null
                            setTextColor(Color.parseColor("#1EC718"))
                            setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f)
                            typeface = font
                        }
                    }
                    val mood = body?.result?.mood ?: Mood.ANXIOUS

                    moodUpdate(mood)
                }else{
                    Log.e("단일 회고 상세 조회", "reponse실패: ${response.code()} - ${response.message()}")
                }
            }catch (e: Exception){
                Log.e("단일 회고 상세 조회", "에러발생: ${ e.message }")
            }

        }
        Log.d("메뉴 확인", "프레그먼트 진입은 함")
        binding.completeLayout.memoirWriteMenu.setOnClickListener {
            Log.d("메뉴 확인", "메뉴클릭되다")
            showMemu(it)
        }
        binding.completeLayout.memoirWriteOk.setOnClickListener {
            // 회고 작성 완료 페이지로 이동
            moveFragment(MemoirListFragment())
        }
        // 뒤로가기
        binding.completeLayout.memoirWriteBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
    /* 프레그먼트 이동 함수 */
    private fun moveFragment(fragment: Fragment){
        requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.main_frm, fragment)
            commit()
        }
    }

    private fun showMemu(view: View){
        val menu = PopupMenu(requireContext(), view)
        menu.menuInflater.inflate(R.menu.memoir_write_menu, menu.menu)

        menu.setOnMenuItemClickListener { item ->
            when(item.itemId){
                R.id.write_modify ->{
                    val bundle = Bundle().apply {
                        val reviewId = body?.result?.id ?: throw IllegalStateException("id = Null입니다")
                        putInt("reviewId", reviewId)
                        putString("mood", body?.result?.mood?.name)
                        putInt("achievement", body?.result!!.achievement)
                        putString("mood", body?.result?.mood?.name)
                        putString("memo", body?.result?.memo)
                        putString("title", body?.result?.title)
                        putString("createdAt", body?.result?.createdAt)
                        putString("description", body?.result?.description)
                    }
                    val fragment = MemoirModifyFragment()
                    fragment.arguments = bundle

                    // 화면 전환
                    moveFragment(fragment)
                    true
                }

                R.id.write_delete -> {
                    // 삭제 API 연결
                    try{
                        lifecycleScope.launch(Dispatchers.IO) {
                            val response = api.memoirDelete(token, reviewId)

                            if(response.isSuccessful) {
                                Log.d("회고 삭제", "성공: ${response}")
                            }else
                                Log.e("회고 삭제", "response 실패: ${response.code()} - ${response.message()}")
                        }
                        // 화면 전환
                        moveFragment(MemoirListFragment())

                    }catch (e: Exception){
                        Log.e("회고 삭제", "${e.message}")
                    }
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
}
