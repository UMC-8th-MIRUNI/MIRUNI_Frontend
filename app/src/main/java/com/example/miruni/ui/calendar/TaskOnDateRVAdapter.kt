package com.example.miruni.ui.calendar

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.R
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.data.Task
import com.example.miruni.databinding.ItemTaskBinding

class TaskOnDateRVAdapter(private val onItemClick: (Task) -> Unit) : RecyclerView.Adapter<TaskOnDateRVAdapter.ViewHolder>() {
    private val tasks = ArrayList<Task>()
    lateinit var binding: ItemTaskBinding
    private lateinit var scheduleDB: ScheduleDatabase

    private var selectState = "unselect"
    private var selectedPos: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemTaskBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        scheduleDB = ScheduleDatabase.getInstance(viewGroup.context)!!

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        holder.bind(tasks[position], position == selectedPos)
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
        fun bind(task: Task, isSelected: Boolean) {
            val schedule = scheduleDB.scheduleDao().getSchedule(task.scheduleId)
            binding.itemScheduleTitleTv.text = schedule.title
            binding.itemTaskTitleTv.text = task.title
            binding.itemSchedulePriorityTv.text = schedule.priority
            binding.itemTaskTimeTv.text = String.format("${task.startTime} - ${task.endTime}")

            if (isSelected) {
                binding.itemTaskItemFrm.setBackgroundResource(R.drawable.bg_ebffe9_main_square_10)
            } else {
                binding.itemTaskItemFrm.setBackgroundResource(R.drawable.bg_eeffed_e7f5e6_square_10)
            }

            binding.root.setOnClickListener {
                val prePos = selectedPos
                selectedPos = this.adapterPosition

                if (prePos != RecyclerView.NO_POSITION) {
                    notifyItemChanged(prePos)
                }
                notifyItemChanged(selectedPos)

                if (selectState == "check" && prePos == selectedPos) {
                    onItemClick(task)
                } else {
                    selectState = "check"
                }
            }
        }
    }
}
