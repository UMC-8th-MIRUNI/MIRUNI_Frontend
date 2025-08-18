package com.example.miruni.ui.memoir

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.R
import com.example.miruni.api.model.ReviewByDate
import com.example.miruni.databinding.ItemMemoirBinding

class MemoirAddRVAdapter(
    private val onItemClick: (Int) -> Unit) :
    RecyclerView.Adapter<MemoirAddRVAdapter.ViewHolder>() {


    private val items = ArrayList<ReviewByDate>()
    private var clickListener: onClickMenuListener? = null

    interface onClickMenuListener {
        fun onClickMenu(reviewId: Int)
    }
    fun setOnClickListener(click: onClickMenuListener){
        clickListener = click
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMemoirBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.memeoirAddTitle.text = items.get(position).title
        holder.binding.meoireItemDate.text = items.get(position).createdAt

        holder.binding.memoirMenuBtn.setOnClickListener {
            showMenu(it, items.get(position).reviewId)
        }
        /* 클릭한 아이템 id 콜 백 */
        holder.binding.memoirItem.setOnClickListener {
            onItemClick(items.get(position).reviewId)
        }
    }
    inner class ViewHolder(val binding: ItemMemoirBinding) : RecyclerView.ViewHolder(binding.root)

    private fun showMenu(view: View, reviewId: Int){
        val menu = PopupMenu(view.context, view)
        menu.menuInflater.inflate(R.menu.memoir_add_menu, menu.menu)

        menu.setOnMenuItemClickListener { item ->
            when(item.itemId){
                R.id.add_delete -> {
                    clickListener?.onClickMenu(reviewId)
                    true
                }
                else -> false
            }
        }
        menu.show()
    }
    fun initRecyclerView(datas: List<ReviewByDate>){
        items.clear()
        items.addAll(datas)
        notifyDataSetChanged()
    }
}
