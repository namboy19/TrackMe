package com.namboy.trackme.utils

import android.content.Context
import android.content.SharedPreferences

class AppPreferences(context: Context) {

    var prefs: SharedPreferences

    init {
        prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
    }

    companion object {
        val PREFS_FILENAME = "com.trackme"
        val PREFS_CURRENT_SESSION = "PREFS_CURRENT_SESSION"
        val PREFS_TRACK_HISTORY = "PREFS_TRACK_HISTORY"
    }

    fun saveString(key: String, value: String) {
        prefs.edit().putString(key, value).commit()
    }

    fun getString(key: String): String {
        return prefs.getString(key, "")?:""
    }

    fun clearCache() {
        prefs.edit().clear().commit()
    }
}