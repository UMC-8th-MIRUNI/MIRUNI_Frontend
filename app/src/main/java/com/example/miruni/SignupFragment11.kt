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
import com.example.miruni.databinding.FragmentSignup11Binding

class SignupFragment11 : Fragment() {

    private val viewModel: SignupViewModel by activityViewModels()
    private var _binding: FragmentSignup11Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignup11Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivButton.setOnClickListener {
            Log.d("SignupFragment11", "회원가입 버튼 클릭됨")
            val name = binding.etName.text.toString()
            val birthday = binding.etBirthday.text.toString()
            val phoneNumber = binding.etPhone.text.toString()

            if (name.isEmpty()) {
                Toast.makeText(requireContext(), "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (birthday.isEmpty()) {
                Toast.makeText(requireContext(), "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (phoneNumber.isEmpty()) {
                Toast.makeText(requireContext(), "비밀번호 확인을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // arguments에서 카카오 로그인 여부 가져오기 (기본 false)
            val isKakao = arguments?.getBoolean("isKakao", false) ?: false
            val kakaoToken = arguments?.getString("kakaoToken") ?: ""

            val signupFragment2 = SignupFragment2().apply {
                arguments = Bundle().apply {
                    putBoolean("isKakao", isKakao)  // 실제 값 전달
                    putString("kakaoToken", kakaoToken)
                }
            }

            // FragmentTransaction으로 이동
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, signupFragment2)
                .addToBackStack(null)
                .commit()

            viewModel.name.value = name
            viewModel.birthday.value = birthday
            viewModel.phoneNumber.value = phoneNumber
        }

        binding.ivSignupBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.viewContent1.setOnClickListener {
            val agreeFragment1 = AgreeFragment1() // 이동할 Fragment 인스턴스
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, agreeFragment1) // Activity 레이아웃의 FrameLayout ID
                .addToBackStack(null) // 뒤로가기 가능
                .commit()
        }

        binding.viewContent2.setOnClickListener {
            val agreeFragment2 = AgreeFragment2() // 이동할 Fragment 인스턴스
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, agreeFragment2) // Activity 레이아웃의 FrameLayout ID
                .addToBackStack(null) // 뒤로가기 가능
                .commit()
        }

        // 초기 상태
        var allAgree = false
        var isAgree2 = false
        var isAgree3 = false

        // 다음 버튼 초기 이미지
        binding.ivButton.setImageResource(R.drawable.greycompletebtn) // 비활성 이미지

        fun updateUI() {
            // 전체 동의 체크박스 상태 업데이트
            allAgree = isAgree2 && isAgree3
            if (allAgree) {
                binding.checkbox1.setImageResource(R.drawable.ion_checkbox)
            } else {
                binding.checkbox1.setImageResource(R.drawable.mdi_check_box_outline_blank)
            }

            // 개별 체크박스 이미지 업데이트
            binding.checkbox2.setImageResource(if (isAgree2) R.drawable.ion_checkbox else R.drawable.mdi_check_box_outline_blank)
            binding.checkbox3.setImageResource(if (isAgree3) R.drawable.ion_checkbox else R.drawable.mdi_check_box_outline_blank)

            // 다음 버튼 활성화 여부
            if (allAgree && isAgree2 && isAgree3) {
                binding.ivButton.setImageResource(R.drawable.next_btn)
            } else {
                binding.ivButton.setImageResource(R.drawable.greycompletebtn)
            }
        }

        // 전체 동의 클릭
        binding.checkbox1.setOnClickListener {
            allAgree = !allAgree
            isAgree2 = allAgree
            isAgree3 = allAgree
            updateUI()
        }

        // 체크박스2 클릭
        binding.checkbox2.setOnClickListener {
            isAgree2 = !isAgree2
            updateUI()
        }

        // 체크박스3 클릭
        binding.checkbox3.setOnClickListener {
            isAgree3 = !isAgree3
            updateUI()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}