package com.example.miruni

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.miruni.databinding.ActivityLoginBinding
import android.util.Log
import androidx.activity.viewModels

class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSignupButton.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }

        binding.ivLoginButton.setOnClickListener {
            val email = binding.loginEmailEt.text.toString()
            val pwd = binding.loginPasswordEt.text.toString()

            if (email.isBlank() || pwd.isBlank()) {
                Toast.makeText(this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(email, pwd)
        }

        // Activity에서는 observe(this)
        viewModel.loginResult.observe(this) { result ->
            result.onSuccess { response ->
                Toast.makeText(this, "로그인 성공!", Toast.LENGTH_SHORT).show()
                Log.d("LoginActivity", "로그인 응답: $response")
//                startActivity(Intent(this, MainActivity::class.java))
//                finish()
            }.onFailure { exception ->
                Toast.makeText(this, "로그인 실패: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

}