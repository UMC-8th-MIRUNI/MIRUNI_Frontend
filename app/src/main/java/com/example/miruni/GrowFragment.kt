package com.example.miruni

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ServiceCompat
import com.example.miruni.databinding.FragmentGrowBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class GrowFragment : Fragment() {

    private lateinit var binding: FragmentGrowBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGrowBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val testTime = arguments?.getString("testTime")?.toLongOrNull() ?: 0L
        val check = arguments?.getBoolean("check") ?: false

        if(check) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val intent = Intent(requireContext(), FocusService::class.java)
                val endTime = System.currentTimeMillis() + testTime
                intent.putExtra("endTime", endTime)
                requireContext().startForegroundService(intent)
            }
        }

        timer(testTime)
        initClickListener()


    }
    /**
     * 이벤트 처리
     */
    private fun initClickListener() {
        binding.growStopBtn.setOnClickListener {
            //일을 언제 마저할지 정하는 페이지
        }

        binding.growCompleteBtn.setOnClickListener {
            (context as ProcessingActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.process_frm, CompleteFragment())
                .commitAllowingStateLoss()
        }
    }
    private fun timer(time: Long){
        object: CountDownTimer(time, 60*1000L){
            override fun onTick(millisUntilFinished: Long) {
                val hour = millisUntilFinished/(60*60*1000L)
                val miniute = (millisUntilFinished / (60*1000L)) % 60

                val leftTime = String.format("%02d:%02d",hour, miniute)
                Log.d("TimerCheck", leftTime)
                binding.growTimer.text = leftTime
            }

            override fun onFinish() {
                Log.d("TimerCheck", "Timer종료")
                binding.growTimer.text = "완료!"
            }
        }.start()
    }
}