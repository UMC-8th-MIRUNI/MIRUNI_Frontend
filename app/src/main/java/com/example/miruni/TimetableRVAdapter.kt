package com.example.miruni

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.databinding.ItemTimetableBinding

class TimetableRVAdapter(): RecyclerView.Adapter<TimetableRVAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TimetableRVAdapter.ViewHolder {
        val binding = ItemTimetableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimetableRVAdapter.ViewHolder, position: Int) {
        // 세부 일정
        holder.binding.timetablePlan
        // 날짜
        holder.binding.timetableDate
        // 수행 시간
        holder.binding.timetableTime
        // 소요 시간
        holder.binding.timetableTimetaken
    }

    override fun getItemCount(): Int { return 0}
    inner class ViewHolder(val binding: ItemTimetableBinding) : RecyclerView.ViewHolder(binding.root)
}