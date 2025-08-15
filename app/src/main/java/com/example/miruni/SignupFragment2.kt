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
import com.example.miruni.databinding.FragmentSignup2Binding
import com.example.miruni.R

class SignupFragment2 : Fragment() {

    private val viewModel: SignupViewModel by activityViewModels()
    private var _binding: FragmentSignup2Binding? = null
    private val binding get() = _binding!!

    private val surveyViewModel: SurveyViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignup2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // observer는 onViewCreated에서 최초 1회만 등록
        viewModel.signupResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { response ->
                Toast.makeText(requireContext(), "회원가입 성공!", Toast.LENGTH_SHORT).show()
                Log.d("SignupFragment2", "회원가입 응답: $response")

                val tokenFromServer = response.result?.accessToken
                surveyViewModel.accessToken.value = tokenFromServer
                Log.d("SignupFragment2", "토큰 세팅: $tokenFromServer")

                // ViewModel에서 nickname 가져오기
                val nicknameFromViewModel = viewModel.nickname.value ?: ""

                // Bundle에 담기
                val bundle = Bundle().apply {
                    putString("nickname", nicknameFromViewModel)
                }


                findNavController().navigate(R.id.action_signupFragment2_to_opening1Fragment, bundle)
            }.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    "회원가입 실패: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("SignupFragment2", "회원가입 실패: ${exception.message}")
            }
        }

        // 닉네임 버튼 클릭 시
        binding.ivNicknameButton.setOnClickListener {
            val nickname = binding.etNickname.text.toString()
            if (nickname.isEmpty()) {
                Toast.makeText(requireContext(), "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.nickname.value = nickname
            viewModel.signup() // 서버 요청
        }

        // 뒤로 가기 버튼
        binding.ivNicknameBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}