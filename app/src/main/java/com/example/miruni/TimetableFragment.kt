package com.example.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miruni.databinding.LayoutTimetableBinding

class TimetableFragment: Fragment() {
    val binding by lazy {
        LayoutTimetableBinding.inflate(layoutInflater)
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

        // recyclerView 연결
        val dapter = TimetableRVAdapter()
        binding.timetableRV.adapter = dapter
        binding.timetableRV.layoutManager = LinearLayoutManager(requireContext())
    }
}