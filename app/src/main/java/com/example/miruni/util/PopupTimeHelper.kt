package com.example.miruni.util

import android.content.Context
import androidx.core.content.edit

object PopupTimeHelper {
    private const val PREF_NAME = "popupTime"
    private const val KEY_HOUR = "popupHour"
    private const val KEY_MINUTE = "popupMinute"

    fun savePopupTime(context: Context, hour: Int, minute: Int) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit() {
            putInt(KEY_HOUR, hour)
            putInt(KEY_MINUTE, minute)
        }
    }

    fun loadPopupTime(context: Context): Pair<Int, Int> {
        val spf = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val hour = spf.getInt(KEY_HOUR, 9)
        val minute = spf.getInt(KEY_MINUTE, 0)
        return hour to minute
    }
}