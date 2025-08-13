package com.example.miruni

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/* 리사이클러뷰 아이템 간 세로 간격 주기 */
class RVSpacer(private val verticalSpacer: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildLayoutPosition(view)
        if( position != parent.adapter?.itemCount?.minus(1)){
            outRect.bottom = verticalSpacer
        }
    }
}