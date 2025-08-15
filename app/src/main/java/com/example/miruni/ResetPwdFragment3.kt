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
import com.example.miruni.databinding.FragmentResetpwd3Binding
import com.example.miruni.R
import kotlinx.coroutines.launch

class ResetPwdFragment3 : Fragment() {

    private lateinit var binding: FragmentResetpwd3Binding
    private val authService by lazy { RetrofitInstance.authService }
    private var email: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResetpwd3Binding.inflate(inflater, container, false)

        // 이전 화면에서 전달받은 이메일
        email = arguments?.getString("email")
        Log.d("ResetPwd", "Fragment3 email=$email")

        binding.send2Btn.setOnClickListener {
            val code = getEnteredCode()

            // 4자리 코드 체크
            if (code.length != 4) {
                Toast.makeText(requireContext(), "4자리 코드를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else if (email.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "이메일 정보가 없습니다.", Toast.LENGTH_SHORT).show()
            } else {
                verifyCode(email!!, code)
            }
        }

        binding.back3Btn.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        return binding.root
    }

    // EditText 4개 값 합치기
    private fun getEnteredCode(): String {
        return listOf(binding.code1, binding.code2, binding.code3, binding.code4)
            .joinToString("") { it.text.toString().trim() }
    }

    // 인증 코드 서버 검증
    private fun verifyCode(email: String, code: String) {
        lifecycleScope.launch {
            try {
                Log.d("ResetPwd", "verifyCode 호출 email=$email, code=$code")
                val response = authService.verifyCode(VerifyCodeRequest(email, code))

                if (response.isSuccessful) {
                    val body = response.body()
                    val resetToken = body?.result?.resetToken

                    if (body?.errorCode == null && !resetToken.isNullOrEmpty()) {
                        Toast.makeText(requireContext(), "인증 성공", Toast.LENGTH_SHORT).show()
                        // resetToken 다음 화면으로 전달
                        val bundle = Bundle().apply { putString("resetToken", resetToken) }
                        findNavController().navigate(
                            R.id.action_resetPwdFragment3_to_resetPwdFragment4,
                            bundle
                        )
                    } else {
                        val msg = body?.message ?: "인증 코드가 올바르지 않습니다."
                        Toast.makeText(requireContext(), "인증 실패: $msg", Toast.LENGTH_SHORT).show()
                        Log.e("ResetPwd", "인증 실패 body=$body")
                    }
                } else {
                    Toast.makeText(requireContext(), "서버 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                    Log.e("ResetPwd", "서버 오류 response.code=${response.code()}, body=${response.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
    }
}