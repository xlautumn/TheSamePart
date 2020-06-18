package com.same.part.assistant.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceUtil(context: Context) {
    companion object {
         const val SEARCH_HISTORY="search_history"

        @Volatile
        private var INSTANCE: SharedPreferenceUtil? = null

        @JvmStatic
        fun getInstance(context: Context): SharedPreferenceUtil {
            return INSTANCE ?: synchronized(this) {
                val instance = SharedPreferenceUtil(context)
                INSTANCE = instance
                instance
            }
        }
    }

    private var preference: SharedPreferences =
        context.getSharedPreferences("SP_SamePart", Context.MODE_PRIVATE)

    fun saveString(key: String, value: String) {
        synchronized(preference) {
            preference.edit().putString(key, value).apply()
        }
    }

    fun getString(key: String): String = synchronized(preference) {
        preference.getString(key, "") ?: ""
    }

    fun removeByKey(key: String){
        preference.edit().remove(key).apply()
    }
}