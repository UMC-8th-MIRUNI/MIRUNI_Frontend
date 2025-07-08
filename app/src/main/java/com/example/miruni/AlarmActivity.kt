package com.example.miruni

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.miruni.databinding.ActivityAlarmBinding

class AlarmActivity: AppCompatActivity() {
    val binding by lazy {
        ActivityAlarmBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}