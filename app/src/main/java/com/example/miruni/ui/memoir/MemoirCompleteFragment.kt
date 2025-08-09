package com.example.miruni.ui.memoir

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.miruni.R
import com.example.miruni.api.ApiService
import com.example.miruni.api.MemoirDetailResponse
import com.example.miruni.api.getRetrofit
import com.example.miruni.data.Mood
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.databinding.FragmentMemoirCompleteBinding
import com.example.miruni.ui.homepage.t
import kotlinx.coroutines.launch


private var body: MemoirDetailResponse? = null
private var db: ScheduleDatabase? = null

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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // 메뉴 이미지 visible전환
        binding.completeLayout.memoirWriteMenu.visibility = View.VISIBLE
        db = ScheduleDatabase.getInstance(requireContext())

        lifecycleScope.launch{
            try{
                val token = "Bearer $t"
                val api = getRetrofit().create(ApiService::class.java)
                val reviewId = requireArguments().getInt("reviewId")
                val response = api.memoirDetail(token, reviewId)
                Log.d("단일 회고 상세 조회", "성공: ${response}")

                if(response.isSuccessful){
                    body = response.body()!!
                    Log.d("단일 회고 상세 조회", "저장된 메모: ${body?.result?.memo}")

                    binding.completeLayout.memoirWriteTitle.text = body?.result?.title
                    binding.completeLayout.memoirWriteDate.text = body?.result?.createdAt
                    binding.completeLayout.memoirWriteTxt.setText(body?.result?.memo ?: "")
                    binding.completeLayout.archievePercent.setText(body?.result?.achievement.toString())
                    val mood = body?.result?.mood ?: Mood.불안

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
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.process_frm, MemoirAddFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
        // 뒤로가기
        binding.completeLayout.memoirWriteBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
    private fun showMemu(view: View){
        val menu = PopupMenu(requireContext(), view)
        menu.menuInflater.inflate(R.menu.memoir_write_menu, menu.menu)

        menu.setOnMenuItemClickListener { item ->
            when(item.itemId){
                // 여기서부터 데이터 연결 진행
                // 0809 -> 회고록 데이터 연결 완성
                R.id.write_modify ->{
                    val bundle = Bundle().apply {
                        val reviewId = body?.result?.id ?: throw IllegalStateException("id = Null입니다")
                        putInt("reviewId", reviewId)
                        putString("mood", body?.result?.mood?.name)
                        /*putInt("achievement", body?.result!!.achievement)
                        putString("mood", body?.result?.mood?.name)
                        putString("memo", body?.result?.memo)
                        putString("title", body?.result?.title)
                        putString("createdAt", body?.result?.createdAt)
                        putString("description", body?.result?.description)*/
                    }
                    val fragment = MemoirModifyFragment()
                    fragment.arguments = bundle

                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.process_frm, fragment)
                    transaction.addToBackStack(null)
                    transaction.commit()
                    true
                }

                R.id.write_delete -> {
                    // 삭제 API 연결
                    true
                }
                else -> false
            }
        }
        menu.show()
    }
    private fun moodUpdate(mood: Mood){
        val check = mood.name
        Log.d("단일 회고 상세 조회", "미루니 감정 확인: ${check}")

        binding.completeLayout.inactiveMood.visibility = View.VISIBLE
        binding.completeLayout.activeMood.visibility = View.INVISIBLE

        when(check){
            "기쁨" -> {
                binding.completeLayout.happyMiruniInactive.visibility = View.INVISIBLE
                binding.completeLayout.happyMiruniActive.visibility = View.VISIBLE
            }
            "슬픔" -> {
                binding.completeLayout.sadMiruniInactive.visibility = View.INVISIBLE
                binding.completeLayout.sadMiruniActive.visibility = View.VISIBLE
            }
            "분노" -> {
                binding.completeLayout.angryMiruniInactive.visibility = View.INVISIBLE
                binding.completeLayout.angryMiruniActive.visibility = View.VISIBLE
            }
            "평온" -> {
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