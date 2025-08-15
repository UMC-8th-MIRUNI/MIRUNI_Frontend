package com.example.miruni.ui.homepage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.R
import com.example.miruni.api.model.TaskItem
import com.example.miruni.api.model.Tasks
import com.example.miruni.data.Task
import com.example.miruni.databinding.ListItemBinding

class HomepageRVAdapter(
    private val datas: List<TaskItem>,
    private val clickItem: (Int) -> Unit
) : RecyclerView.Adapter<HomepageRVAdapter.ViewHolder>() {

    var deleteMode = false
    private var seletedItems = mutableSetOf<Int>() // 선택된 task id저장

    interface onplayClickListener{
        fun onPlayClick(planId: Int) // 진행 화면으로 이동
        fun onMemoirClick(reviewId: Int) // 회고 화면으로 이동
    }

    private var listener: onplayClickListener? = null

    fun setOnClickListener(playClickListener: onplayClickListener){
        listener = playClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.task.text = datas[position].title
        /* 상태에 따라 이미지 및 텍스트 변경 */
        when(datas[position].staus){
            Status.STOP.toString() -> {
                holder.binding.homepageTaskStatus.setImageResource(R.drawable.homepage_stop_status)
                holder.binding.timeStatus.text = "중지 시간:"
                holder.binding.goalTime.text = datas[position].stoppedAt

                // 버튼 이미지 바꾸고 실행 중 화면으로 이동
                holder.binding.playBtn.setImageResource(R.drawable.homepage_play)
                holder.binding.playBtn.setOnClickListener { listener?.onPlayClick(datas[position].planId) }
            }
            Status.COMPLETE.toString() -> {
                holder.binding.homepageTaskStatus.setImageResource(R.drawable.homepage_completed_status)
                holder.binding.timeStatus.text = "완료시간:"
                holder.binding.goalTime.text = datas[position].pausedAt

                // 버튼 이미지 바꾸고 회고 페이지로 이동
                holder.binding.playBtn.setImageResource(R.drawable.homepage_review_btn)
                holder.binding.playBtn.setOnClickListener { listener?.onMemoirClick(datas[position].reviewId ?: 0) }
            }
            Status.NOT_STARTED.toString() -> {
                holder.binding.homepageTaskStatus.setImageResource(R.drawable.homepage_expected_staus)
                holder.binding.timeStatus.text = "예정 시간:"
                holder.binding.goalTime.text = datas[position].scheduledStart

                // 버튼 이미지 바꾸고 실행 중 화면으로 이동
                holder.binding.playBtn.setImageResource(R.drawable.homepage_play)
                holder.binding.playBtn.setOnClickListener { listener?.onPlayClick(datas[position].planId) }
            }
        }

        /* 리스트 클릭 시 TimetableFragment로 콜 백 */
        holder.binding.taskBg.setOnClickListener { clickItem(datas[position].planId) }


        // 체크박스 활성화
        holder.binding.homepageTaskStatus.visibility = if(!deleteMode) View.VISIBLE else View.GONE
        holder.binding.taskCheckbox.visibility = if(deleteMode) View.VISIBLE else View.GONE

        holder.binding.taskCheckbox.isChecked = seletedItems.contains(position)

        holder.binding.taskCheckbox.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked)
                seletedItems.add(datas[position].planId)
            Log.d("삭제 리스트 콜백", "삭제 리스트 콜백: @${seletedItems.toList()}")
        }

    }
    override fun getItemCount(): Int = datas.size

    fun deleteItem(state: Boolean){
        deleteMode = state
        notifyDataSetChanged()
    }


    inner class ViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root)
}
// status 상태 저장
enum class Status {
    NOT_STARTED,
    COMPLETE,
    STOP
}