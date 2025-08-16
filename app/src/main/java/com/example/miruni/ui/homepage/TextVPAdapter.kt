package com.example.miruni.ui.homepage

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.databinding.WisesayingItemBinding


class TextVPAdapter(private val item: List<String>):
    RecyclerView.Adapter<TextVPAdapter.VPViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VPViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = WisesayingItemBinding.inflate(inflater, parent, false)
        return VPViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VPViewHolder, position: Int) {
        holder.itemBinding.wisesayingTxt.text = item[position]
    }

    override fun getItemCount(): Int = item.size

    inner class VPViewHolder(val itemBinding: WisesayingItemBinding) : RecyclerView.ViewHolder(itemBinding.root)

}