package com.example.miruni

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.miruni.databinding.ActivityFullscreenBinding

class FullscreenActivity: AppCompatActivity() {
    val binding by lazy {
        ActivityFullscreenBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.fullBack.setOnClickListener {
            val intent = Intent(this, ProcessingActivity::class.java)
            intent.putExtra("showFragment", "GrowFragment")
            startActivity(intent)

        }
    }
}