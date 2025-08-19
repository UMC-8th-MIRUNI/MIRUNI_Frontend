package com.example.miruni

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.data.Task
import com.example.miruni.databinding.ItemTimetableBinding

class TimetableRVAdapter(private val db: ScheduleDatabase): RecyclerView.Adapter<TimetableRVAdapter.ViewHolder>() {
    private val tasks = ArrayList<Task>()
    var scheduleId: Int = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TimetableRVAdapter.ViewHolder {
        val binding = ItemTimetableBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimetableRVAdapter.ViewHolder, position: Int) {

        holder.bind(tasks[position])

        // 세부 일정
        holder.binding.timetablePlan
        // 날짜
        holder.binding.timetableDate
        // 수행 시간
        holder.binding.timetableTime
        // 소요 시간
        holder.binding.timetableTimetaken
    }

    override fun getItemCount(): Int = tasks.size

    fun addTasks(scheduleId: Int) {
        this.scheduleId = scheduleId
        tasks.clear()
        tasks.addAll(db.taskDao().getTasksByScheduleId(scheduleId))
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemTimetableBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {
            val tmpDate = task.executeDay.split("-")
            binding.timetableDate.text = String.format("${tmpDate[1]}/${tmpDate[2]}")
            binding.timetablePlan.text = task.title

            var tmpTime = task.endTime.split(":")
            val end = tmpTime[0].toInt() * 60 + tmpTime[1].toInt()
            tmpTime = task.startTime.split(":")
            val start = tmpTime[0].toInt() * 60 + tmpTime[1].toInt()

            binding.timetableTime.apply {
                if ((end - start) > 60) {
                    text = String.format("${ (end - start) % 60 }시간 ${(end - start)/60}분")
                    textSize = 9f
                } else {
                    text = String.format("${end-start}분")

                }
            }

            binding.timetableTimetaken.text = String.format("${task.startTime.substring(0, 5)}-${task.endTime.substring(0, 5)}")
        }
    }
}