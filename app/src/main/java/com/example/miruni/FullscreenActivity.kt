package com.example.miruni

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.miruni.databinding.ActivityFullscreenBinding
import com.example.miruni.ui.calendar.ScheduleExecutionFragment

class FullscreenActivity: AppCompatActivity() {
    val binding by lazy {
        ActivityFullscreenBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.fullBack.setOnClickListener {
            val fragment = ScheduleExecutionFragment()
            val id = intent.getIntExtra("executedId", -1)

            val spf = this.getSharedPreferences("executedTask", MODE_PRIVATE)
            val editor = spf.edit()
            editor.putInt("taskId", id)
            editor.apply()

            supportFragmentManager.beginTransaction()
                .replace(R.id.main_frm, fragment)
                .commitAllowingStateLoss()
        }
    }
}