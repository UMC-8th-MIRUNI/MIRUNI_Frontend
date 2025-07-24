package com.example.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miruni.databinding.FragmentMemoirAddBinding

class MemoirAddFragment: Fragment() {
    val binding by lazy {
        FragmentMemoirAddBinding.inflate(layoutInflater)
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
        val dummyList = listOf(
            Task("umc", "14:00", "[회계원리] 레포트 과제 (1)", "expected"),
            Task("umc", "15:30", "[자료구조] 강의 정리", "fail"),
            Task("umc", "17:00", "[UI/UX] 와이어프레임 작성", "complete"),
            Task("umc", "15:30", "[회계원리] 레포트 과제 (1)", "expected"),
            Task("umc", "14:00", "[UI/UX] 와이어프레임 작성", "delay"),
            Task("umc", "14:00", "[자료구조] 강의 정리", "expected"),
            Task("umc", "14:00", "[회계원리] 레포트 과제 (1)", "complete"),
            Task("umc", "14:00", "[회계원리] 레포트 과제 (1)", "fail")
        )

        val dapter = MemoirAddRVAdapter(dummyList) { clickedTask ->
            val bundle = Bundle().apply {
                putString("content", clickedTask.content)
                putString("date", clickedTask.date)
            }
            val transaction = parentFragmentManager.beginTransaction()
            val fragment = MemoirCompleteFragment()
                fragment.arguments = bundle

            transaction.replace(R.id.process_frm, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        binding.memeoirAddRv.adapter = dapter
        binding.memeoirAddRv.layoutManager = LinearLayoutManager(requireContext())


        binding.memoirAddBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.process_frm, MemoirWriteFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
}