package com.example.miruni.ui.homepage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.R
import com.example.miruni.api.model.Category
import com.example.miruni.api.model.DeleteTaskRequest
import com.example.miruni.api.model.TaskItem
import com.example.miruni.api.model.Tasks
import com.example.miruni.data.Review
import com.example.miruni.data.Task
import com.example.miruni.databinding.ListItemBinding

class HomepageRVAdapter(private val clickItem: (Int) -> Unit ) : RecyclerView.Adapter<HomepageRVAdapter.ViewHolder>() {

    private val datas = ArrayList<TaskItem>()

    var deleteMode = false
    private var reviewDatas: List<Review> = emptyList()

    private var seletedItems = mutableSetOf<DeleteTaskRequest>()
    //private var seletedItems = mutableSetOf<Int>() // 선택된 task id저장

    interface onplayClickListener{
        fun onPlayClick(planId: Int) // 진행 화면으로 이동
        fun onMemoirClick(reviewId: Int, planId: Int, aiPlainId: Int?) // 회고 화면으로 이동
        fun ondeleteTask(request: DeleteTaskRequest)
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
        when(datas[position].status){
            Status.PAUSED.toString() -> {
                holder.binding.homepageTaskStatus.setImageResource(R.drawable.homepage_stop_status)
                holder.binding.timeStatus.text = "중지 시간:"
                holder.binding.goalTime.text = datas[position].stoppedAt
                holder.binding.stoppedAt.text = datas[position].pausedAt

                // 버튼 이미지 바꾸고 실행 중 화면으로 이동
                holder.binding.playBtn.setImageResource(R.drawable.homepage_play)
                holder.binding.playBtn.setOnClickListener { listener?.onPlayClick(datas[position].planId) }
            }
            Status.FINISHED.toString() -> {
                holder.binding.homepageTaskStatus.setImageResource(R.drawable.homepage_completed_status)
                holder.binding.timeStatus.text = "완료시간:"
                holder.binding.goalTime.text = datas[position].stoppedAt

                val writed = reviewDatas.any{ it.planId == datas[position].planId }

                if(datas[position].reviewId != null){ // 버튼 이미지 바꾸고 회고 완료 페이지로 이동
                    holder.binding.playBtn.setImageResource(R.drawable.homepage_review_btn)
                    holder.binding.playBtn.setOnClickListener { listener?.onMemoirClick(datas[position].reviewId!!, datas[position].planId, datas[position].aiPlanId) }
                }else{  // 버튼 이미지 바꾸고 회고 미작성 페이지로 이동
                    holder.binding.playBtn.setImageResource(R.drawable.homepage_not_write)
                    holder.binding.playBtn.setOnClickListener { listener?.onMemoirClick(0, datas[position].planId, datas[position].aiPlanId) }
                }
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

        //holder.binding.taskCheckbox.isChecked = seletedItems.contains(position)

        holder.binding.taskCheckbox.setOnClickListener {
            val isChecked = holder.binding.taskCheckbox.isChecked

            val list = listOf(datas[position].aiPlanId)
            if(isChecked) {
                seletedItems.add(
                    DeleteTaskRequest(
                        datas[position].category,
                        datas[position].planId,
                        list
                    )
                )
            }
            else {
                seletedItems.remove(
                    DeleteTaskRequest(
                        datas[position].category,
                        datas[position].planId,
                        list
                    )
                )
            }

            Log.d("삭제 리스트 콜백", "삭제 리스트 콜백: ${seletedItems.toList()}")
        }

    }
    override fun getItemCount(): Int = datas.size

    fun deleteItem(state: Boolean){
        deleteMode = state
        notifyDataSetChanged()
    }
    fun getSelectedItems(): Set<DeleteTaskRequest>{
        return seletedItems
    }
    fun updateData(updateList: List<TaskItem>){
        datas.clear()
        datas.addAll(updateList)
        notifyDataSetChanged()
    }
    inner class ViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root)
}
// status 상태 저장
enum class Status {
    NOT_STARTED,
    FINISHED,
    PAUSED
}