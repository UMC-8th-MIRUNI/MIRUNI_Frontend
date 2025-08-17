package com.example.miruni.ui.calendar

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.R
import com.example.miruni.data.Plan
import com.example.miruni.data.Priority
import com.example.miruni.data.ScheduleDatabase
import com.example.miruni.databinding.ItemTaskBinding
import com.example.miruni.util.splitDateTimeHelper

class TaskOnDateRVAdapter(private val onItemClick: (Plan) -> Unit) : RecyclerView.Adapter<TaskOnDateRVAdapter.ViewHolder>() {
    private val plans = ArrayList<Plan>()
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
        holder.bind(plans[position], position == selectedPos)
    }

    override fun getItemCount(): Int = plans.size

    @SuppressLint("NotifyDataSetChanged")
    fun addTask(tasks: ArrayList<Plan>) {
        this.plans.clear()
        this.plans.addAll(tasks)

        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun deleteAllTasks() {
        this.plans.clear()

        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemTaskBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(plan: Plan, isSelected: Boolean) {
            if (plan.parentTitle == null) {
                binding.itemScheduleTitleTv.text = plan.title
                binding.itemTaskCheckIv.visibility = View.GONE
                binding.itemTaskTitleTv.visibility = View.GONE
            } else {
                binding.itemScheduleTitleTv.text = plan.parentTitle
                binding.itemTaskTitleTv.text = plan.title
            }

            Log.d("Calendar", "${plan.scheduledStart} - ${plan.scheduledEnd}")

            binding.itemSchedulePriorityTv.text = Priority.valueOf(plan.priority!!).localLabel
            binding.itemTaskTimeTv.text = String.format("${splitDateTimeHelper(plan.scheduledStart.toString(), false)} - ${splitDateTimeHelper(plan.scheduledEnd.toString(), false)}")

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
                    onItemClick(plan)
                } else {
                    selectState = "check"
                }
            }
        }
    }
}