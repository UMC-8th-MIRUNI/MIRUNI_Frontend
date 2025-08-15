package com.example.miruni

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.miruni.databinding.FragmentResetpwd1Binding
import com.example.miruni.R
import kotlinx.coroutines.launch


class ResetPwdFragment1 : Fragment() {

    private lateinit var binding: FragmentResetpwd1Binding
    private val authService by lazy { RetrofitInstance.authService }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetpwd1Binding.inflate(inflater, container, false)

        binding.nextBtn.setOnClickListener {
            val email = binding.editTextText.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(requireContext(), "이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(requireContext(), "유효한 이메일을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                sendVerificationCode(email) // 여기서만 호출, navigate는 sendVerificationCode 안에서만 처리
            }
        }

        binding.backBtn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun sendVerificationCode(email: String) {
        lifecycleScope.launch {
            try {
                val response = authService.sendVerificationCode(ResetPwdRequest(email))
                if (response.isSuccessful && response.body()?.errorCode == null) {
                    Toast.makeText(requireContext(), "인증 코드가 발송되었습니다.", Toast.LENGTH_SHORT).show()
                    val bundle = Bundle().apply { putString("email", email) }
                    findNavController().navigate(R.id.action_resetPwdFragment1_to_resetPwdFragment2, bundle)
                } else {
                    val msg = response.body()?.message ?: "이메일 전송 실패"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류 발생", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }
}