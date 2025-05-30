package com.mlab.knockme.core.util

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

inline fun <reified T> SharedPreferences.saveObject(key: String, obj: T) {
    val json = Gson().toJson(obj)
    edit {
        putString(key, json)
    }
}

inline fun <reified T> SharedPreferences.readObject(key: String): T? {
    val json = getString(key, null) ?: return null
    return try {
        Gson().fromJson<T>(json, object : TypeToken<T>() {}.type)
    } catch (_: Exception) {
        null
    }
}

object PrefKeys {
    const val USER_INFO = "user_info"
    const val PROGRAM_LIST = "programs"
    const val SHOW_HADITH = "show_hadith"
}
