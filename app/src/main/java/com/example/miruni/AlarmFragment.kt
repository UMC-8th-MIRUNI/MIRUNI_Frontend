package com.example.miruni

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.miruni.data.Alarm
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.databinding.FragmentAlarmBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmFragment: Fragment() {

    val binding by lazy {
        FragmentAlarmBinding.inflate(layoutInflater)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }
    private lateinit var db : ScheduleDatabase
    private lateinit var alarmDatas: List<Alarm>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("AlarmFragment Data Check", "알람 프레그먼트 호출 됐다~")

        db = ScheduleDatabase.getInstance(requireContext())!!
        lifecycleScope.launch(Dispatchers.IO) {
            alarmDatas = db.alarmDao().getAllAlarm()

            if(alarmDatas.isEmpty()){
                Log.d("AlarmFragment Data Check", "저장된 알람 데이터 없음")
            }else{
                alarmDatas.forEach { Log.d("AlarmFragment Data Check", "$it") }
            }

            val adapter = AlarmRVAdapter(alarmDatas)
            binding.alarmRV.adapter = adapter
            val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.alarmRV.layoutManager = layoutManager
            val spacer = resources.getDimensionPixelSize(R.dimen.recycler_dimen)
            binding.alarmRV.addItemDecoration(RVSpacer(spacer))
        }


    }
}