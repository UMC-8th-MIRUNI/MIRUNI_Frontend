package com.example.miruni

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.databinding.ItemPinnedBinding

class PinnedRVAdapter: RecyclerView.Adapter<PinnedRVAdapter.ViewHolder>() {
    private val pinnedItems = ArrayList<Schedule>()
    lateinit var binding: ItemPinnedBinding

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PinnedRVAdapter.ViewHolder {
        binding = ItemPinnedBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PinnedRVAdapter.ViewHolder, position: Int) {
        holder.bind(pinnedItems[position])

        holder.apply {
            binding.itemTodoMoreBtn.setOnClickListener{
                Log.d("Pinned", "Go to Detail")
            }
        }
    }

    override fun getItemCount(): Int = pinnedItems.size

    @SuppressLint("NotifyDataSetChanged")
    fun addSchedule(pinnedItems: ArrayList<Schedule>) {
        this.pinnedItems.clear()
        this.pinnedItems.addAll(pinnedItems)

        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemPinnedBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(schedule: Schedule) {
            binding.itemTodoTitleTv.text = schedule.title
            binding.itemTodoDateTv.text = schedule.date
        }
    }
}