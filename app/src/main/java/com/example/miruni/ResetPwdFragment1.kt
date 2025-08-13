package com.example.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.miruni.databinding.FragmentResetpwd1Binding
import com.example.miruni.R


class ResetPwdFragment1 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentResetpwd1Binding.inflate(inflater, container, false)

        binding.ivLoginButton.setOnClickListener {
            findNavController().navigate(R.id.action_resetPwdFragment1_to_resetPwdFragment2)
        }
        return binding.root
    }
}