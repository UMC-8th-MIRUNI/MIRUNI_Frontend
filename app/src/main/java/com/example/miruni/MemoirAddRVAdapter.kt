package com.example.miruni

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.data.Task
import com.example.miruni.databinding.ItemMemoirBinding

class MemoirAddRVAdapter(
    private val items: List<Task>,
    private val onItemClick: (Task) -> Unit) :
    RecyclerView.Adapter<MemoirAddRVAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMemoirBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.memeoirAddTitle.text = item.title
        holder.binding.meoireItemDate.text = item.startTime

        holder.binding.memoirMenuBtn.setOnClickListener {
            showMenu(it)
        }
        holder.binding.memoirBack.setOnClickListener {
            onItemClick(item)
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