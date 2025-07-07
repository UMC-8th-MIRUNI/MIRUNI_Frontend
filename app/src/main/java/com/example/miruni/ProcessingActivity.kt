package com.example.miruni

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.miruni.databinding.ActivityProcessingBinding

class ProcessingActivity : AppCompatActivity() {

    private lateinit var binding : ActivityProcessingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProcessingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initProcessing()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        initProcessing()
    }

    private fun initProcessing() {
        val show = intent.getStringExtra("showFragment")
        when (show) {
//            "StartFragment" -> {
//                transitionFragment(StartFragment())
//            }
            "CommitFragment" -> {
                transitionFragment(CommitFragment())
            }
//            "GrowFragment" -> {
//                transitionFragment(GrowFragment())
//            }
        }
    }

    private fun transitionFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.process_frm, fragment)
            .commitAllowingStateLoss()
    }
}