package com.example.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import androidx.fragment.app.DialogFragment
import com.example.miruni.databinding.FragmentLoadingBinding

class LoadingFragment : DialogFragment() {

    private var _binding: FragmentLoadingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoadingBinding.inflate(inflater, container, false)

        /** 애니메이션 설정 */
        initAnimation()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpWindowAttributes()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initAnimation(){
        val animationSet = AnimationSet(true)
        var offsetCount = 500

        for (i in 0..1) {
            val left = AnimationUtils.loadAnimation(context, R.anim.vibrate_left)
            left.duration = 500
            left.startOffset = offsetCount.toLong()
            animationSet.addAnimation(left)
            offsetCount += 500

            val right = AnimationUtils.loadAnimation(context, R.anim.vibrate_right)
            right.duration = 500
            right.startOffset = offsetCount.toLong()
            animationSet.addAnimation(right)
            offsetCount += 500
        }

        binding.loadingMiruniIv.startAnimation(animationSet)
    }

    private fun setUpWindowAttributes() {
        val params = dialog?.window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        params?.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params
        dialog?.window?.setBackgroundDrawableResource(R.color.main)
    }
}