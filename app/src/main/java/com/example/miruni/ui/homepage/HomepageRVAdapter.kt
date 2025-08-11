package com.example.miruni.ui.homepage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.R
import com.example.miruni.data.Task
import com.example.miruni.databinding.ListItemBinding

class HomepageRVAdapter(
    private val datas: List<Task>
) : RecyclerView.Adapter<HomepageRVAdapter.ViewHolder>() {

    var deleteMode = false
    private var seletedItems = mutableSetOf<Int>() // 선택된 task id저장

    interface onplayClickListener{
        //fun onPlayClick(isDone: Boolean)
        fun onPlayClick(isDone: String) // 진행화면 혹은 회고 화면으로 이동
        fun onDeleteItem(seletedItems: List<Int>)   // 삭제 task 반환
    }

    private lateinit var listener: onplayClickListener

    fun setOnClickListener(playClickListener: onplayClickListener){
        listener = playClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.task.text = datas[position].title
        holder.binding.goalTime.text = datas[position].startTime

        // 체크박스 활성화
        holder.binding.homepageTaskStatus.visibility = if(!deleteMode) View.VISIBLE else View.GONE
        holder.binding.taskCheckbox.visibility = if(deleteMode) View.VISIBLE else View.GONE

        holder.binding.taskCheckbox.isChecked = seletedItems.contains(position)

        holder.binding.taskCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
                seletedItems.add(datas[position].id)
            Log.d("삭제 리스트 콜백", "삭제 리스트 콜백: @${seletedItems.toList()}")
            listener.onDeleteItem(seletedItems.toList())
        }


        // isDone 상태에 따라 회고록 상태 바꾸기(예정, 중지, 완료)
        if (datas[position].status === "") {
            holder.binding.playBtn.setImageResource(R.drawable.homepage_not_write)
            holder.binding.playBtn.setOnClickListener {
                // 회고 작성 페이지로 이동
                listener.onPlayClick(datas[position].status ?: "상태확인")


            }
        } else if (datas[position].status == "완료") {
            holder.binding.playBtn.setImageResource(R.drawable.homepage_review_btn)
            holder.binding.playBtn.setOnClickListener {
                // 회고 완료 페이지로 이동
                listener.onPlayClick(datas[position].status ?: "상태확인")


            }
        }
        holder.binding.playBtn.setOnClickListener {
            holder.binding.playBtn.setOnClickListener {
                // 진행 페이지로 이동
                listener.onPlayClick(datas[position].status ?: "상태확인")


            }
        }

    }
    override fun getItemCount(): Int = datas.size

    fun deleteItem(state: Boolean){
        deleteMode = state
        notifyDataSetChanged()
    }


    inner class ViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root)
}
