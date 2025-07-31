package com.example.calendartest

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.MainActivity
import com.example.miruni.data.Schedule
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.data.Task
import com.example.miruni.databinding.ItemTaskBinding

class TaskOnDateRVAdapter : RecyclerView.Adapter<TaskOnDateRVAdapter.ViewHolder>() {
    private val taskItems = ArrayList<Pair<Schedule,Task>>()
    lateinit var binding: ItemTaskBinding

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemTaskBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(taskItems[position])

        holder.apply {
            binding.itemTaskItemFrm.setOnClickListener{
                TODO() // 일정 상세 소개로 이동
            }
        }
    }

    override fun getItemCount(): Int = taskItems.size

    @SuppressLint("NotifyDataSetChanged")
    fun addTask(taskItems: ArrayList<Pair<Schedule, Task>>) {
        this.taskItems.clear()
        this.taskItems.addAll(taskItems)

        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(taskItem: Pair<Schedule, Task>) {
            binding.itemScheduleTitleTv.text = taskItem.first.title
            binding.itemTaskTitleTv.text = taskItem.second.title
            binding.itemSchedulePriorityTv.text = taskItem.first.priority
            binding.itemTaskTimeTv.text = buildString {
                append(taskItem.second.startTime)
                append(taskItem.second.endTime)
            }
        }
    }
}
