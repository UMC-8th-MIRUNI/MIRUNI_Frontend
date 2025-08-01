package com.example.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.miruni.databinding.FragmentSignup5Binding
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.activityViewModels

class SignupFragment5 : Fragment() {

    private val viewModel: SignupViewModel by activityViewModels()
    private lateinit var binding: FragmentSignup5Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignup5Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivCompleteButton.setOnClickListener {
            val selected = mutableListOf<String>()
            if (binding.checkboxOption1.isChecked) selected.add("KTYPE")
            if (binding.checkboxOption2.isChecked) selected.add("LTYPE")
            if (binding.checkboxOption3.isChecked) selected.add("MTYPE")
            if (binding.checkboxOption4.isChecked) selected.add("NTYPE")
            if (binding.checkboxOption5.isChecked) selected.add("OTYPE")
            if (binding.checkboxOption6.isChecked) {
                val custom = binding.editTextCustom.text.toString()
                if (custom.isNotBlank()) selected.add(custom)
            }

            viewModel.addPreferences(selected)
            Log.d("SignupFragment5", "가입 시도: ${selected.joinToString()}") // 확인용 로그

            viewModel.signup() // 화면 이동은 아래 observe에서 처리
        }
        viewModel.signupResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { response ->
                Toast.makeText(requireContext(), "회원가입 성공!", Toast.LENGTH_SHORT).show()
                Log.d("SignupFragment5", "회원가입 응답: $response")

                // 성공 후 화면 이동
                findNavController().navigate(R.id.action_signupFragment5_to_opening2Fragment)
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "회원가입 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("SignupFragment5", "회원가입 실패: ${exception.message}")
            }
        }


        binding.ivSelectBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}