package com.example.miruni.util

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.miruni.MainActivity
import com.example.miruni.R

/**
 * 하단 네비게이션 바 숨기기
 */
fun controlBottomNavigation(activity: MainActivity, show: Boolean) {
    val navigationBar = activity.findViewById<ConstraintLayout>(R.id.main_nav)

    if (show) {
        navigationBar.visibility = View.VISIBLE
    } else {
        navigationBar.visibility = View.GONE
    }
}

fun controlTopBar(activity: MainActivity, show: Boolean) {
    val topBar = activity.findViewById<ConstraintLayout>(R.id.main_top_bar)

    if (show) {
        topBar.visibility = View.VISIBLE
    } else {
        topBar.visibility = View.GONE
    }
}