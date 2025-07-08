package com.example.miruni

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
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, LockFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}