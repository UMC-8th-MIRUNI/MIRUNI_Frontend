package com.example.miruni

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.miruni.databinding.FragmentSignup3Binding
import com.example.miruni.R

class SignupFragment3 : Fragment() {

    private val viewModel: SurveyViewModel by activityViewModels()
    private lateinit var binding: FragmentSignup3Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignup3Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivCompleteButton.setOnClickListener {
            val selected = mutableListOf<String>()
            if (binding.checkboxOption1.isChecked) selected.add("PHONE")
            if (binding.checkboxOption2.isChecked) selected.add("VIDEO")
            if (binding.checkboxOption3.isChecked) selected.add("MEET_FRIENDS")
            if (binding.checkboxOption4.isChecked) selected.add("TOO_MUCH_WORK")
            if (binding.checkboxOption5.isChecked) selected.add("TOO_TIRED")

            viewModel.situations.value = selected
            val signupFragment4 = SignupFragment4()

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, signupFragment4) // Activity의 FrameLayout ID
                .addToBackStack(null) // 뒤로가기 가능
                .commit()
        }

        binding.ivSelectBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}