package com.example.miruni.ui.tool

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.miruni.R
import com.example.miruni.databinding.FragmentBlockGuideBinding

class BlockGuideFragment: Fragment() {
    val binding by lazy {
        FragmentBlockGuideBinding.inflate(layoutInflater)
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

        (activity?.findViewById<View>(R.id.main_top_bar))?.visibility = View.GONE
        (activity?.findViewById<View>(R.id.main_nav))?.visibility = View.GONE

        binding.checkLayout.visibility = View.VISIBLE
        binding.nextLayout.visibility = View.GONE

        binding.checkLayout.setOnClickListener {
            binding.nextLayout.visibility = View.VISIBLE
            binding.checkLayout.visibility = View.GONE


            binding.skipBtn.setOnClickListener {
                /* 넘어가기 */
            }
            binding.nextBtn.setOnClickListener {
                /* 타이머 설정 화면 */
                moveFragment(BlockStartFragment())
            }
        }

    }
    private fun moveFragment(fragment: Fragment){
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, fragment)
            .addToBackStack(null)
            .commit()
    }
}