package com.example.miruni

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.miruni.databinding.FragmentLoginBinding
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch
import retrofit2.Response
import com.example.miruni.RetrofitInstance.authService
import java.security.MessageDigest
import android.util.Base64

class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.tvSignupButton.setOnClickListener {
            startActivity(Intent(requireContext(), SignupActivity::class.java))
        }

        binding.ivLoginButton.setOnClickListener {
            val email = binding.loginEmailEt.text.toString()
            val pwd = binding.loginPasswordEt.text.toString()

            if (email.isBlank() || pwd.isBlank()) {
                Toast.makeText(requireContext(), "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, pwd)
        }

        binding.ivKakaoButton.setOnClickListener {
            startKakaoLogin()
        }

        // Fragment에서는 viewLifecycleOwner 사용
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { response ->
                Toast.makeText(requireContext(), "로그인 성공!", Toast.LENGTH_SHORT).show()
                Log.d("LoginFragment", "로그인 응답: $response")
                /*startActivity(Intent(MainActivity::class.java))
                finish()*/
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "로그인 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvFindPassword.setOnClickListener {
            // NavController로 화면 전환
            findNavController().navigate(R.id.action_loginFragment_to_resetPwdFragment1)
        }
    }

    private fun startKakaoLogin() {
        val context = requireContext()

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    loginWithKakaoAccount(context)
                } else if (token != null) {
                    verifyAndSendToken(token.accessToken)
                }
            }
        } else {
            loginWithKakaoAccount(context)
        }
    }

    private fun loginWithKakaoAccount(context: Context) {
        UserApiClient.instance.loginWithKakaoAccount(context) { token, error ->
            if (error != null) {
                Toast.makeText(context, "카카오 로그인 실패: ${error.message}", Toast.LENGTH_SHORT).show()
            } else if (token != null) {
                verifyAndSendToken(token.accessToken)
            }
        }
    }

    private fun verifyAndSendToken(accessToken: String) {
        // 토큰 유효성 체크
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Log.e("KakaoLogin", "토큰 유효성 검사 실패", error)
                Toast.makeText(requireContext(), "유효하지 않은 토큰입니다.", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("KakaoLogin", "토큰 유효함: userId=${tokenInfo?.id}")

                // 서버로 토큰 전송
                sendTokenToServer(accessToken)
            }
        }
    }

    private fun sendTokenToServer(kakaoAccessToken: String) {
        lifecycleScope.launch {
            try {
                val response = authService.loginWithKakao(KakaoLoginRequest(kakaoAccessToken))

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.errorCode == null && body?.result != null) {
                        Toast.makeText(requireContext(), "로그인 성공!", Toast.LENGTH_SHORT).show()

                        val accessToken = body.result.accessToken
                        val refreshToken = body.result.refreshToken

                        saveTokens(accessToken, refreshToken)

                        // 다음 화면 이동 등 처리
                    } else {
                        Toast.makeText(requireContext(), "서버 오류: ${body?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("Login", "서버 응답 실패: ${response.code()}, $errorBody")
                    Toast.makeText(requireContext(), "서버 응답 실패: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "네트워크 오류: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveTokens(accessToken: String?, refreshToken: String?) {
        val prefs = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString("access_token", accessToken ?: "")
            putString("refresh_token", refreshToken ?: "")
            apply()
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}