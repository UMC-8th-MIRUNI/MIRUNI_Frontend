package com.example.miruni

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.databinding.ListItemBinding

class ListVPAdapter(private val pagedItems: List<List<Map<String, String>>>) :
    RecyclerView.Adapter<ListVPAdapter.PageViewHolder>() {

    inner class PageViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(items: List<Map<String, String>>) {
            val innerAdapter = InnerListAdapter(items)
            binding.innerRecyclerView.adapter = innerAdapter
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(pagedItems[position])
    }

    override fun getItemCount(): Int = pagedItems.size
}
