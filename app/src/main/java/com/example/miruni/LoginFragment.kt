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
import androidx.activity.result.contract.ActivityResultContracts
import com.example.miruni.TokenManager.saveToken
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task

class LoginFragment : Fragment() {
    private val viewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    //구글 로그인
    private lateinit var googleSignInClient: GoogleSignInClient

    // Activity Result API 사용
    private val googleSignInLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        setupGoogleSignIn()

        binding.ivGoogleButton.setOnClickListener {
            val signInIntent = googleSignInClient.signInIntent
            googleSignInLauncher.launch(signInIntent)
        }
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

                val accessToken = response.result?.accessToken
                val refreshToken = response.result?.refreshToken
                Log.d("token", accessToken.toString())
                saveToken(requireContext(), accessToken.toString())

                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }.onFailure { exception ->
                Toast.makeText(requireContext(), "로그인 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvFindPassword.setOnClickListener {
            // NavController로 화면 전환
            findNavController().navigate(R.id.action_loginFragment_to_resetPwdFragment1)
        }
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("80560946457-k5cv74758cu6eo4pb1vr9isu5vheqcki.apps.googleusercontent.com") // 반드시 웹 클라이언트 ID
//            .requestIdToken("80560946457-2e7vdcu4h3ck607k5b5ov9d9tjfv4gnf.apps.googleusercontent.com") // com.example.miruni
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account?.idToken
            val email = account?.email
            Log.d("GoogleLogin", "idToken=$idToken, email=$email")

            if (idToken != null) {
                // 서버로 토큰 전송 후 로그인 처리
                sendGoogleTokenToServer(idToken)
            }

        } catch (e: ApiException) {
            Log.e("GoogleLogin", "signInResult:failed code=${e.statusCode}", e)
            Toast.makeText(requireContext(), "Google 로그인 실패: code=${e.statusCode}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendGoogleTokenToServer(idToken: String) {
        lifecycleScope.launch {
            try {
                val response = authService.loginWithGoogle(GoogleLoginRequest(idToken))

                if (response.isSuccessful) {
                    val body = response.body()
                    val result = body?.result

                    if (result != null) {
                        if (!result.isNewUser && !result.isPending) {
                            // 이미 회원가입 되어 있음 → 로그인 완료
                            saveTokens(result.accessToken, result.refreshToken)
                            Toast.makeText(requireContext(), "로그인 성공!", Toast.LENGTH_SHORT).show()

                            // 메인 화면 이동
//                        startActivity(Intent(requireContext(), MainActivity::class.java))
//                        requireActivity().finish()

                        } else {
                            // 회원가입 또는 추가 정보 입력 필요
                            // 구글에서 가져온 이메일은 LoginFragment에서 account.email로 전달 가능
                            val emailFromGoogle = GoogleSignIn.getLastSignedInAccount(requireContext())?.email ?: ""

                            val bundle = Bundle().apply {
                                putString("email", emailFromGoogle)
                                putString("googleToken", idToken) // 서버 회원가입용 토큰
                            }

                            findNavController().navigate(
                                R.id.action_loginFragment_to_fragment_signup11,
                                bundle
                            )
                        }
                    } else {
                        Toast.makeText(requireContext(), "서버 로그인 실패: ${body?.message}", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(requireContext(), "서버 응답 오류: ${response.code()}", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("GoogleLogin", "서버 통신 오류", e)
                Toast.makeText(requireContext(), "서버 통신 오류", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startKakaoLogin() {
        val context = requireContext()

        if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
            UserApiClient.instance.loginWithKakaoTalk(context) { token, error ->
                if (error != null) {
                    loginWithKakaoAccount(context)
                } else if (token != null) {
                    requestKakaoUserInfo(token.accessToken)
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
                requestKakaoUserInfo(token.accessToken)
            }
        }
    }

    // 로그인 성공 후 사용자 정보 요청
    private fun requestKakaoUserInfo(accessToken: String) {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e("KakaoLogin", "사용자 정보 요청 실패", error)
            } else if (user != null) {
                val email = user.kakaoAccount?.email
                val nickname = user.kakaoAccount?.profile?.nickname
                Log.d("KakaoLogin", "사용자 이메일: $email, 닉네임: $nickname")

                // 서버로 토큰 전송
                sendTokenToServer(accessToken)
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