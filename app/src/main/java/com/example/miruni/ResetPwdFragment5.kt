package com.example.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import com.example.miruni.databinding.FragmentResetpwd5Binding
import com.example.miruni.R

class ResetPwdFragment5 : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentResetpwd5Binding.inflate(inflater, container, false)

        binding.tologinBtn.setOnClickListener {
            val loginFragment = LoginFragment()
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, loginFragment)
                .commit()
        }

        binding.back5Btn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }
}