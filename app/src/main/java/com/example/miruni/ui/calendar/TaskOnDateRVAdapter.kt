package com.example.miruni.ui.calendar

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.data.Task
import com.example.miruni.databinding.ItemTaskBinding

class TaskOnDateRVAdapter : RecyclerView.Adapter<TaskOnDateRVAdapter.ViewHolder>() {
    private val tasks = ArrayList<Task>()
    lateinit var binding: ItemTaskBinding
    private lateinit var scheduleDB: ScheduleDatabase

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemTaskBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        scheduleDB = ScheduleDatabase.getInstance(viewGroup.context)!!

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(tasks[position])

        holder.apply {
            binding.itemTaskItemFrm.setOnClickListener{
                TODO() // 일정 상세 소개로 이동
            }
        }
    }

    override fun getItemCount(): Int = tasks.size

    @SuppressLint("NotifyDataSetChanged")
    fun addTask(tasks: ArrayList<Task>) {
        this.tasks.clear()
        this.tasks.addAll(tasks)

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteAllTasks() {
        this.tasks.clear()

        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            val schedule = scheduleDB.scheduleDao().getSchedule(task.scheduleId)
            binding.itemScheduleTitleTv.text = schedule.title
            binding.itemTaskTitleTv.text = task.title
            binding.itemSchedulePriorityTv.text = schedule.priority
            binding.itemTaskTimeTv.text = String.format("${task.startTime} - ${task.endTime}")
        }
    }
}
