package com.example.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.miruni.databinding.FragmentSignup5Binding

class SignupFragment5 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignup5Binding.inflate(inflater, container, false)

        binding.ivCompleteButton.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment5_to_opening2Fragment)
        }

        binding.ivSelectBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

}