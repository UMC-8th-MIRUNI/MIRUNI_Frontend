package com.example.miruni.ui.memoir

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.api.ReviewDate
import com.example.miruni.data.Review
import com.example.miruni.databinding.ItemMemoitListBinding

class MemoirListRVAdapter(
    private val item: List<ReviewDate>,
    private val itemClick: (String) -> Unit
): RecyclerView.Adapter<MemoirListRVAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = ItemMemoitListBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.memoirDate.text = item[position].date
        holder.binding.memoirCount.text = item[position].count.toString()

        /* 선택한 아이템 날짜 콜 백 */
        holder.binding.dateList.setOnClickListener { itemClick(item[position].date) }
    }

    override fun getItemCount() = item.size

    inner class ViewHolder(val binding: ItemMemoitListBinding): RecyclerView.ViewHolder(binding.root)
}
