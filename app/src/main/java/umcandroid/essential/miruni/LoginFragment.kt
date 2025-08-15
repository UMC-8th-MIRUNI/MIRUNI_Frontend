package umcandroid.essential.miruni

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
import umcandroid.essential.miruni.databinding.FragmentLoginBinding
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch
import retrofit2.Response
import umcandroid.essential.miruni.RetrofitInstance.authService
import java.security.MessageDigest
import android.util.Base64
import androidx.fragment.app.activityViewModels
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

//    private val viewModel2: SurveyViewModel by activityViewModels()

    //구글
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 1001  // 요청 코드

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        // 구글 로그인 옵션 설정
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))  // 발급받은 클라이언트 ID
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        binding.ivGoogleButton.setOnClickListener {
            signInWithGoogle()
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
                saveTokens(accessToken, refreshToken)

//                startActivity(Intent(requireContext(), MainActivity::class.java))
//                requireActivity().finish()

            }.onFailure { exception ->
                Toast.makeText(requireContext(), "로그인 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvFindPassword.setOnClickListener {
            // NavController로 화면 전환
            findNavController().navigate(R.id.action_loginFragment_to_resetPwdFragment1)
        }
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            // 성공: account.idToken 사용 가능
            val idToken = account?.idToken
            Log.d("GoogleLogin", "ID Token: $idToken")

            // 서버로 토큰 전송 후 로그인 처리
            idToken?.let { sendGoogleTokenToServer(it) }

        } catch (e: ApiException) {
            Log.e("GoogleLogin", "signInResult:failed code=" + e.statusCode)
            Toast.makeText(requireContext(), "Google 로그인 실패", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendGoogleTokenToServer(idToken: String) {
        // 서버 API 호출: Retrofit, Coroutine 등으로 구현
        Log.d("GoogleLogin", "서버로 보낼 토큰: $idToken")
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
        // 카카오 토큰 유효성 체크
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Log.e("KakaoLogin", "❌ 토큰 유효성 검사 실패", error)
                Toast.makeText(requireContext(), "유효하지 않은 토큰입니다.", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("KakaoLogin", "✅ 토큰 유효함: userId=${tokenInfo?.id}")
                Log.d("KakaoLogin", "📩 카카오에서 받은 accessToken: $accessToken")

                // 카카오 API 직접 호출해서 검증 (프론트에서 한 번 더 체크)
                lifecycleScope.launch {
                    try {
                        val testUrl = "https://kapi.kakao.com/v1/user/access_token_info"
                        val conn = java.net.URL(testUrl).openConnection() as java.net.HttpURLConnection
                        conn.requestMethod = "GET"
                        conn.setRequestProperty("Authorization", "Bearer $accessToken")
                        val code = conn.responseCode
                        val body = conn.inputStream.bufferedReader().readText()
                        Log.d("KakaoLogin", "🔍 카카오 API 직접 검증 응답 코드: $code")
                        Log.d("KakaoLogin", "🔍 카카오 API 직접 검증 응답 바디: $body")
                    } catch (e: Exception) {
                        Log.e("KakaoLogin", "카카오 API 직접 검증 실패", e)
                    }
                }

                // 서버로 토큰 전송
                sendTokenToServer(accessToken)
            }
        }
    }

    private fun sendTokenToServer(kakaoAccessToken: String) {
        lifecycleScope.launch {
            try {
                Log.d("Login", "📤 서버로 보낼 카카오 토큰: $kakaoAccessToken")
                Log.d("Login", "📤 요청 JSON: ${KakaoLoginRequest(kakaoAccessToken)}")

                val response = authService.loginWithKakao(KakaoLoginRequest(kakaoAccessToken))

                Log.d("Login", "📥 서버 raw 응답 코드: ${response.code()}")
                Log.d("Login", "📥 서버 raw 응답 바디: ${response.errorBody()?.string() ?: response.body()}")

                if (response.isSuccessful) {
                    val body = response.body()
                    if (body?.errorCode == null && body?.result != null) {
                        Toast.makeText(requireContext(), "로그인 성공!", Toast.LENGTH_SHORT).show()
                        val accessToken = body.result.accessToken
                        val refreshToken = body.result.refreshToken
                        saveTokens(accessToken, refreshToken)
                    } else {
                        Toast.makeText(requireContext(), "서버 오류: ${body?.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("Login", "❌ 서버 응답 실패: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("Login", "네트워크 오류", e)
            }
        }
    }


    private fun saveTokens(accessToken: String?, refreshToken: String?) {
        val prefs = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putString("access_token", accessToken ?: "")
            putString("refresh_token", refreshToken ?: "")
            apply()

            Log.d("CheckToken", "AccessToken=$accessToken, RefreshToken=$refreshToken")
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}