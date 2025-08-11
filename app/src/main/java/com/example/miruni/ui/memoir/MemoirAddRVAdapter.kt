package com.example.miruni.ui.memoir

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.R
import com.example.miruni.api.ReviewByDate
import com.example.miruni.data.Task
import com.example.miruni.databinding.ItemMemoirBinding

class MemoirAddRVAdapter(
    private val items: List<ReviewByDate>,
    private val onItemClick: (Int) -> Unit) :
    RecyclerView.Adapter<MemoirAddRVAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMemoirBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.memeoirAddTitle.text = items.get(position).title
        holder.binding.meoireItemDate.text = items.get(position).createdAt

        holder.binding.memoirMenuBtn.setOnClickListener {
            showMenu(it)
        }
        // 클릭하면 MemoirCompleteFragment로 넘어감
        holder.binding.memoirItem.setOnClickListener {
            onItemClick(items.get(position).id)
        }
    }
    inner class ViewHolder(val binding: ItemMemoirBinding) : RecyclerView.ViewHolder(binding.root)

    private fun showMenu(view: View){
        val menu = PopupMenu(view.context, view)
        menu.menuInflater.inflate(R.menu.memoir_add_menu, menu.menu)

        menu.setOnMenuItemClickListener { item ->
            when(item.itemId){
                R.id.add_modify -> {
                    true
                }
                R.id.add_delete -> {
                    true
                }
                else -> false
            }
        }
        menu.show()
    }
}
