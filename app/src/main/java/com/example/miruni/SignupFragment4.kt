package com.example.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.miruni.databinding.FragmentSignup4Binding

class SignupFragment4 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignup4Binding.inflate(inflater, container, false)

        binding.ivCompleteButton.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment4_to_signupFragment5)
        }

        binding.ivSelectBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

}