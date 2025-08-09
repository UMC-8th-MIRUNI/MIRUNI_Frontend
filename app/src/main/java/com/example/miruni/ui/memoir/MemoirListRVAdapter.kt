package com.example.miruni.ui.memoir

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.data.Review
import com.example.miruni.databinding.ItemMemoitListBinding

class MemoirListRVAdapter(private val item: List<Review>): RecyclerView.Adapter<MemoirListRVAdapter.ViewHolder>() {

    interface OnMemoirItemClick{
        fun onItemClick(review: Review)
    }
    private lateinit var itemClick: OnMemoirItemClick

    fun setOnItemClick(itemClickListener: OnMemoirItemClick){
        itemClick = itemClickListener
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemMemoitListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.memoirDate.text = item[position].createdAt
        holder.binding.memoirCount.text = item[position].title
        holder.binding.dateList.setOnClickListener { itemClick.onItemClick(item[position]) }
    }

    override fun getItemCount() = item.size

    inner class ViewHolder(val binding: ItemMemoitListBinding): RecyclerView.ViewHolder(binding.root)
}