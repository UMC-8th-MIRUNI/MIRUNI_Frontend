package com.example.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.miruni.databinding.FragmentSignup2Binding

class SignupFragment2 : Fragment() {

    private val viewModel: SignupViewModel by activityViewModels()
    private var _binding: FragmentSignup2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignup2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivNicknameButton.setOnClickListener {
            val nickname = binding.etNickname.text.toString()
            if (nickname.isEmpty()) {
                Toast.makeText(requireContext(), "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.nickname.value = nickname
            findNavController().navigate(R.id.action_signupFragment2_to_opening1Fragment)
        }

        binding.ivNicknameBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}