package com.example.miruni

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.miruni.databinding.ActivityMypageBinding

class MyPageActivity: AppCompatActivity() {
    val binding by lazy {
        ActivityMypageBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}