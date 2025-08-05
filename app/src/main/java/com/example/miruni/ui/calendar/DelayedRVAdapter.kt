package com.example.miruni.ui.calendar

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.data.Schedule
import com.example.miruni.databinding.ItemDelayedBinding

class DelayedRVAdapter: RecyclerView.Adapter<DelayedRVAdapter.ViewHolder>() {
    private val delayedItems = ArrayList<Schedule>()
    lateinit var binding: ItemDelayedBinding

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemDelayedBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(delayedItems[position])

        holder.apply {
            binding.itemDelayedMoreBtn.setOnClickListener{
                Log.d("delayed", "Go to Detail")
            }
        }
    }

    override fun getItemCount(): Int = delayedItems.size

    @SuppressLint("NotifyDataSetChanged")
    fun addSchedule(delayedItems: ArrayList<Schedule>) {
        this.delayedItems.clear()
        this.delayedItems.addAll(delayedItems)

        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemDelayedBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(schedule: Schedule) {
            binding.itemDelayedTitleTv.text = schedule.title
            binding.itemDelayedDateTv.text = schedule.date
        }
    }
}