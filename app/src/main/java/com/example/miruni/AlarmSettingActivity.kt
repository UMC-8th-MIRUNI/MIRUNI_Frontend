package com.example.miruni

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.miruni.databinding.ActivityAlarmsettingBinding

class AlarmSettingActivity: AppCompatActivity() {
    val binding by lazy {
        ActivityAlarmsettingBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}