package com.example.miruni

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.miruni.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, CalendarFragment())
            .commitAllowingStateLoss()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent.let {
            when(it.getStringExtra("showFragment")) {
                "ScheduleFragment" -> {
                    transitionFragment(ScheduleFragment())
                }
                "CalendarFragment" -> {
                    transitionFragment(CalendarFragment())
                }
            }
        }
    }

    private fun transitionFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, fragment)
            .commitAllowingStateLoss()
    }
}