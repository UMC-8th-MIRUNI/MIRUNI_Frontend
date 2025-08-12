package com.example.miruni

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.miruni.databinding.ActivityLoginBinding
import android.util.Log
import androidx.activity.viewModels

class LoginActivity : AppCompatActivity() {
    val viewModel: SignupViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}