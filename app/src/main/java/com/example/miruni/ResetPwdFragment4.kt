package com.example.miruni

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.miruni.databinding.FragmentResetpwd4Binding
import com.example.miruni.R
import kotlinx.coroutines.launch

class ResetPwdFragment4 : Fragment() {

    private lateinit var binding: FragmentResetpwd4Binding
    private val authService by lazy { RetrofitInstance.authService }
    private var resetToken: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetpwd4Binding.inflate(inflater, container, false)

        // 이전 Fragment3에서 전달받은 resetToken
        resetToken = arguments?.getString("resetToken")

        binding.resetBtn.setOnClickListener {
            val newPassword = binding.editTextText2.text.toString().trim()
            val confirmPassword = binding.editTextText3.text.toString().trim()

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show()
            } else if (newPassword != confirmPassword) {
                Toast.makeText(requireContext(), "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show()
            } else {
                resetPassword(newPassword)
            }
        }

        binding.back4Btn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    private fun resetPassword(newPassword: String) {
        if (resetToken.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "resetToken이 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                // 디버깅용 로그
                Log.d("ResetPwdFragment4", "resetToken=$resetToken")
                Log.d("ResetPwdFragment4", "newPassword=$newPassword")

                val response = authService.completeResetPassword(
                    resetToken ?: "",
                    ResetPwdCompleteRequest(newPassword)
                )

                // 서버 응답 로그
                Log.d("ResetPwdFragment4", "response.code=${response.code()}")
                Log.d("ResetPwdFragment4", "response.body=${response.body()}")
                Log.d("ResetPwdFragment4", "response.errorBody=${response.errorBody()?.string()}")

                if (response.isSuccessful && response.body()?.errorCode == null) {
                    Toast.makeText(requireContext(), "비밀번호 재설정 완료", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack(R.id.loginFragment, false)
                } else {
                    val msg = response.body()?.message ?: "비밀번호 재설정 실패"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    Log.e("ResetPwdFragment4", "비밀번호 재설정 실패: $msg")
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }

}