package com.example.miruni

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.toColorInt
import com.example.miruni.databinding.FragmentMypageBinding

class MypageFragment : Fragment() {

    private lateinit var binding : FragmentMypageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMypageBinding.inflate(layoutInflater, container, false)

        val topBar = (activity as MainActivity).findViewById<ConstraintLayout>(R.id.main_top_bar)
        topBar.setBackgroundColor("#E8FAE5".toColorInt())

        return inflater.inflate(R.layout.fragment_mypage, container, false)
    }
}