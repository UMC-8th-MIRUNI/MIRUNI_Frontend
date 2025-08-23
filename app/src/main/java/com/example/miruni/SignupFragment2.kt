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

    private var isKakaoLogin: Boolean = false
    private var isAgree: Boolean = false

    private val surveyViewModel: SurveyViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fragment arguments에서 Kakao 로그인 여부 가져오기
        isKakaoLogin = arguments?.getBoolean("isKakao", false) ?: false
        Log.d("SignupFragment2", "onCreate: isKakaoLogin = $isKakaoLogin")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignup2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 체크박스 초기 상태 & 클릭 이벤트
        isAgree = viewModel.agreedPrivacyPolicy.value ?: false
        updateCheckboxUI()
        binding.checkbox.setOnClickListener {
            isAgree = !isAgree
            viewModel.agreedPrivacyPolicy.value = isAgree
            updateCheckboxUI()
        }

        // 카카오 회원가입 결과 observer
        viewModel.kakaoSignupResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { response ->
                handleSignupSuccess(response.result.accessToken)
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "카카오 회원가입 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("SignupFragment2", "카카오 회원가입 실패", exception)
            }
        }

        // 일반 회원가입 결과 observer
        viewModel.signupResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { response ->
                handleSignupSuccess(response.result?.accessToken)
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "회원가입 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("SignupFragment2", "회원가입 실패", exception)
            }
        }

        // 닉네임 버튼 클릭
        binding.ivNicknameButton.setOnClickListener {
            val nickname = binding.etNickname.text.toString()
            if (nickname.isEmpty()) {
                Toast.makeText(requireContext(), "닉네임을 입력하세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.nickname.value = nickname

            if (!isAgree) {
                Toast.makeText(requireContext(), "개인정보 동의를 체크해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 안전하게 Kakao token 가져오기
            val kakaoToken = arguments?.getString("kakaoToken")
            Log.d("SignupFragment2", "버튼 클릭: isKakaoLogin = $isKakaoLogin, kakaoToken = $kakaoToken")

            if (isKakaoLogin) {
                if (kakaoToken.isNullOrEmpty()) {
                    Toast.makeText(requireContext(), "카카오 토큰이 없습니다.", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                viewModel.kakaoSignup(kakaoToken)
            } else {
                viewModel.signup()
            }
        }

        // 뒤로가기 버튼
        binding.ivNicknameBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun updateCheckboxUI() {
        binding.checkbox.setImageResource(
            if (isAgree) R.drawable.ion_checkbox
            else R.drawable.mdi_check_box_outline_blank
        )
    }

    private fun handleSignupSuccess(accessToken: String?) {
        Toast.makeText(requireContext(), "회원가입 성공!", Toast.LENGTH_SHORT).show()
        Log.d("SignupFragment2", "회원가입 성공! 토큰: $accessToken")
        surveyViewModel.accessToken.value = accessToken

        val nicknameFromViewModel = viewModel.nickname.value ?: ""
        val bundle = Bundle().apply { putString("nickname", nicknameFromViewModel) }

        val opening1Fragment = Opening1Fragment()
        opening1Fragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, opening1Fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}