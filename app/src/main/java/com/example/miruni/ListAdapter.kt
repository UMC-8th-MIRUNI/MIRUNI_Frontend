package com.example.miruni

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.databinding.ListItemBinding

class ListAdapter(private val items: List<Map<String, String>>) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // 오늘의 일정 데이터 연결
        fun bind(item: Map<String, String>) {
            binding.task.text = item["task"]
            binding.goalTime.text = item["time"]
            when(item["status"]){
                "1" -> binding.homepageTaskStatus.setImageResource(R.drawable.homepage_expected_staus_iv)
                "2" -> binding.homepageTaskStatus.setImageResource(R.drawable.homepage_dealy_staus_iv)
                "3" -> binding.homepageTaskStatus.setImageResource(R.drawable.homepage_fail_status_iv)
                "4" -> binding.homepageTaskStatus.setImageResource(R.drawable.homepage_complete_status_iv)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}