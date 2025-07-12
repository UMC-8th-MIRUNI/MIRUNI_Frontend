package com.example.miruni

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.miruni.databinding.FragmentLockBinding

class LockFragment : Fragment() {
    val binding by lazy { FragmentLockBinding.inflate(layoutInflater) }
    val fragment = GrowFragment()
    val bundle = Bundle()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClickListener()

    }
    private fun initClickListener(){
        binding.lockNoTv.setOnClickListener {
            bundle.putBoolean("check", false)
            moveFragment()
        }
        binding.lockYesTv.setOnClickListener {
            bundle.putBoolean("check", true)
            moveFragment()
        }
    }
    private fun moveFragment(){
        // testTime 값 받아와야됨
        val testTime = 11 * 60 * 1000L

        bundle.putString("testTime", "$testTime")
        fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.process_frm, fragment)
            .addToBackStack(null)
            .commit()
    }
}
