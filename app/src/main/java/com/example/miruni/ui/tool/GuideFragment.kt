package com.example.miruni.ui.tool

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.miruni.R
import com.example.miruni.databinding.FragmentBlockBinding

class GuideFragment: Fragment() {
    val binding by lazy {
        FragmentBlockBinding.inflate(layoutInflater)
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
        (activity?.findViewById<View>(R.id.main_nav))?.visibility = View.GONE
        (activity?.findViewById<View>(R.id.main_top_bar))?.visibility = View.GONE

        val planId = arguments?.getInt("idForCheckSplit") ?: -1

        /* 실행 중 화면으로 이동 */
        binding.checkLayout.setOnClickListener {
            val fragment = BlockStartFragment()
            val bundle = Bundle()
            bundle.putInt("idForCheckSplit", planId)
            bundle.putBoolean("blockCheck", true)
            fragment.arguments = bundle
            requireActivity().supportFragmentManager.beginTransaction().apply {
                replace(R.id.main_frm, BlockStartFragment())
                addToBackStack(null)
                commit()
            }
        }
    }
}