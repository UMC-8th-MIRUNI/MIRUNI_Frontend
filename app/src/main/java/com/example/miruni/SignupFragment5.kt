package com.example.miruni

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.miruni.RetrofitInstance.authService
import com.example.miruni.R

class SignupFragment5 : Fragment() {

    private var _binding: FragmentSignup5Binding? = null
    private val binding get() = _binding!!
    private val viewModel: SurveyViewModel by activityViewModels()
    private val repository = SurveyRepository(authService)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignup5Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 체크박스 선택값 ViewModel에 저장 + 서버 전송
        binding.ivCompleteButton.setOnClickListener {
            val selected = mutableListOf<String>()
            if (binding.checkboxOption1.isChecked) selected.add("LAZY")
            if (binding.checkboxOption2.isChecked) selected.add("TOO_BIG_TO_START")
            if (binding.checkboxOption3.isChecked) selected.add("DONT_KNOW_WHERE_TO_START")
            if (binding.checkboxOption4.isChecked) selected.add("PERFECTIONISM")
            if (binding.checkboxOption5.isChecked) selected.add("CANT_CONCENTRATE")
            if (binding.checkboxOption6.isChecked) selected.add("NOT_FUN")

            viewModel.reasons.value = selected

            // 서버 전송
            viewModel.submitSurvey(repository)
        }

        // 서버 응답 관찰
        viewModel.surveyResult.observe(viewLifecycleOwner) { response ->
            response.result?.let {
                Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()

                // Fragment가 attach 되어 있는지 확인 후 navigate
                if (isAdded) {
                    try {
                        findNavController().navigate(R.id.action_signupFragment5_to_opening2Fragment)
                    } catch (e: Exception) {
                        Log.e("SignupFragment5", "Navigation error: ${e.message}")
                    }
                }
            }
        }

        // 오류 처리
        viewModel.error.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}