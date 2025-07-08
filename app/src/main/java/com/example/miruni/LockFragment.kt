package com.example.miruni

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.miruni.databinding.FragmentLockBinding

class LockFragment : Fragment() {
    val binding by lazy { FragmentLockBinding.inflate(layoutInflater) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val testTime: Long = 1 * 60 * 1000L  // 1분
        val endTime = System.currentTimeMillis() + testTime

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(requireContext(), FocusService::class.java).apply {
                putExtra("endTime", endTime)
            }
            requireContext().startForegroundService(intent)
        }
    }
}
