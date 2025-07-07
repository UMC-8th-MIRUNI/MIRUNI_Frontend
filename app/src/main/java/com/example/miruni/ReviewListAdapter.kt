package com.example.miruni

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.miruni.databinding.ItemReviewListBinding

class ReviewListRVAdapter(): RecyclerView.Adapter<ReviewListRVAdapter.ViewHolder>() {

//    private val reviews = ArrayList<Review>()

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): ReviewListRVAdapter.ViewHolder {
        val binding: ItemReviewListBinding = ItemReviewListBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewListRVAdapter.ViewHolder, position: Int) {
//        holder.bind(reviews[position])
    }

    override fun getItemCount(): Int {
//        return reviews.size
        return TODO("Provide the return value")
    }

    inner class ViewHolder(val binding: ItemReviewListBinding) : RecyclerView.ViewHolder(binding.root) {
//        fun bind(review: Review) {
//            binding.reviewlistDateTv.text = review.date
//            binding.reviewlistTitleTv.text = review.title
//            binding.reviewlistDescriptionTv.text = review.desc
//        }
    }
}