package com.example.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miruni.databinding.FragmentHomepageBinding

class HomepageFragment: Fragment() {
    val binding by lazy {
        FragmentHomepageBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dummyList = listOf(
            mapOf("start" to "12:00", "end" to "14:00", "task" to "[회계원리] 레포트 과제 (1)"),
            mapOf("start" to "14:30", "end" to "15:30", "task" to "[자료구조] 강의 정리"),
            mapOf("start" to "16:00", "end" to "17:00", "task" to "[UI/UX] 와이어프레임 작성")
        )

        val adapter = ListAdapter(dummyList)
        binding.listRV.adapter = adapter
        binding.listRV.layoutManager = LinearLayoutManager(requireContext())
    }
}