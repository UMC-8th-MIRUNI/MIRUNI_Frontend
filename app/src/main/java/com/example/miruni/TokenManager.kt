package com.example.miruni

import android.content.Context
import android.util.Log

object TokenManager {

    private const val PREFS_NAME = "auth_prefs"
    private const val TOKEN_KEY = "jwt_token"

    fun saveToken(context: Context, token: String) {
        Log.d("토큰확인: ", "$token")
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    fun getToken(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(TOKEN_KEY, null)
    }
}