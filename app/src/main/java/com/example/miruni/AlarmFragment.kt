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
import kotlinx.coroutines.withContext

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
        (activity?.findViewById<View>(R.id.main_top_bar))?.visibility = View.GONE
        (activity?.findViewById<View>(R.id.main_nav))?.visibility = View.GONE


        db = ScheduleDatabase.getInstance(requireContext())!!
        lifecycleScope.launch(Dispatchers.IO) {
            alarmDatas = db.alarmDao().getAllAlarm()
            withContext(Dispatchers.Main){
                val adapter = AlarmRVAdapter(alarmDatas)
                binding.alarmRV.adapter = adapter
                val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                binding.alarmRV.layoutManager = layoutManager
                val spacer = resources.getDimensionPixelSize(R.dimen.recycler_dimen)
                binding.alarmRV.addItemDecoration(RVSpacer(spacer))
            }
        }

        binding.alarmBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}