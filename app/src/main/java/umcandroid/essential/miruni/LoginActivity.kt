/*package umcandroid.essential.miruni

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.miruni.MainActivity
import com.example.miruni.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 바인딩 초기화
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSignupButton.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        binding.ivLoginButton.setOnClickListener {
            login()

//            val authService = AuthService()
//            authService.setLoginView(this)
//
//            authService.login(LoginRequest(email, pwd))

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    //
    private fun login() {
        if (binding.loginEmailEt.text.toString().isEmpty()) {
            Toast.makeText(this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        if (binding.loginPasswordEt.text.toString().isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val email: String = binding.loginEmailEt.text.toString()
        val pwd: String = binding.loginPasswordEt.text.toString()

    }


    //
    private fun saveJwt2(jwt:String){
        val spf = getSharedPreferences("auth2", MODE_PRIVATE)
        val editor = spf.edit()

        editor.putString("jwt", jwt)
        editor.apply()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

//    override fun onLoginSuccess(code: String, result: Result) {
//        when(code){
//            "COMMON200"-> {
//                saveJwt2(result.accessToken)
//                startMainActivity()
//            }
//        }
//    }

//
//    override fun onLoginFailure(message : String) {
//        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
//    }


}*/
