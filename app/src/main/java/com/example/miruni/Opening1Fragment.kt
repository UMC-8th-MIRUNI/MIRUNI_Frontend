package com.example.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.miruni.databinding.FragmentOpening1Binding

class Opening1Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentOpening1Binding.inflate(inflater, container, false)

        binding.ivOpeningButton.setOnClickListener {
            findNavController().navigate(R.id.action_opening1Fragment_to_signupFragment3)
        }

        binding.ivSignupBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

}