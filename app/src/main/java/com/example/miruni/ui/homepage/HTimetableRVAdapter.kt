package com.example.miruni.ui.homepage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.R
import com.example.miruni.TimetableRVAdapter
import com.example.miruni.api.SplitSchedule
import com.example.miruni.data.Task
import com.example.miruni.databinding.ItemTimetableBinding

class HTimetableRVAdapter: RecyclerView.Adapter<HTimetableRVAdapter.ViewHolder>() {
    private val scheduleList = ArrayList<SplitSchedule>()
    private var aiPlanId = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HTimetableRVAdapter.ViewHolder {
        val binding = ItemTimetableBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HTimetableRVAdapter.ViewHolder, position: Int) {

        // 세부 일정
        holder.binding.timetablePlan.text = scheduleList[position].description
        // 날짜
        val date = scheduleList[position].date.split("-")
        holder.binding.timetableDate.text = "${date[1]}/${date[2]}"
        // 수행 시간
        val startTime = scheduleList[position].startTime.split(":")
        val endTime = scheduleList[position].endTime.split(":")
        holder.binding.timetableTime.text = "${startTime[0]}:${startTime[1]}-${endTime[0]}:${endTime[1]}"
        // 소요 시간
        holder.binding.timetableTimetaken.text = "${scheduleList[position].expectedDuration}분"

        if(scheduleList[position].planId == aiPlanId){
            holder.binding.timetableList.setBackgroundResource(R.color.select_item)
        }
    }

    override fun getItemCount(): Int = scheduleList.size

    fun updateData(datas: List<SplitSchedule>, aiPlanId: Int){
        this.aiPlanId = aiPlanId
        scheduleList.clear()
        scheduleList.addAll(datas)
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemTimetableBinding) : RecyclerView.ViewHolder(binding.root)
}