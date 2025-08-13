package com.example.miruni

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.data.Alarm
import com.example.miruni.data.AlarmType
import com.example.miruni.databinding.ItemAlarmBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlarmRVAdapter(private val items: List<Alarm>): RecyclerView.Adapter<AlarmRVAdapter.ViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.alarmTitle.text = items[position].title
        holder.binding.alamrContent.text = items[position].content

        holder.binding.alarmIcon.setImageResource(
            when(items[position].alarmType){
                AlarmType.POPUP -> R.drawable.popup_icon
                AlarmType.BANNER -> R.drawable.banner_icon
            }
        )
        /* currentTimeMillis 변환 */
        val time = items[position].time.toLongOrNull() ?: 0L
        val min = SimpleDateFormat("mm", Locale.getDefault()).format(Date(time))
        val minStr = min.format(items[position].time)

        holder.binding.alarmTime.text = "${minStr}분 전"
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemAlarmBinding): RecyclerView.ViewHolder(binding.root)
}