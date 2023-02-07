package com.mlab.knockme.core.util

import android.content.SharedPreferences
import android.os.Parcelable
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

/**
 * Set a [Parcelable] value in the preferences editor, to be written back once
 * [SharedPreferences.Editor.commit] or [SharedPreferences.Editor.apply] are called.
 * @author Arnau Mora
 * @param key The name of the preference to modify.
 * @param parcelable The new value for the preference. Passing null for this argument is equivalent
 * to calling [SharedPreferences.Editor.remove] with this key.
 */
fun SharedPreferences.Editor.putParcelable(key: String, parcelable: Parcelable?) {
    val json = Gson().toJson(parcelable)
    putString(key, json)
}

/**
 * Retrieve a [T] value from the preferences.
 * @author Arnau Mora
 * @param key The name of the preference to retrieve.
 * @param default The value to return if the preference doesn't exist, or is not valid.
 * @return Returns the preference value if it exists, or [default] if not.
 */
inline fun <reified T : Parcelable?> SharedPreferences.getParcelable(key: String, default: T): T {
    val json = getString(key, null)
    return try {
        if (json != null)
            Gson().fromJson(json, T::class.java)
        else default
    } catch (_: JsonSyntaxException) {
        default
    }
}


/**
 * Set a [Parcelable] [Collection] value in the preferences editor, to be written back once
 * [SharedPreferences.Editor.commit] or [SharedPreferences.Editor.apply] are called.
 * @author Arnau Mora
 * @param key The name of the preference to modify.
 * @param collection The new value for the preference. Passing null for this argument is equivalent
 * to calling [SharedPreferences.Editor.remove] with this key.
 */
fun SharedPreferences.Editor.putParcelableList(key: String, collection: Collection<Parcelable>) {
    val json = Gson().toJson(collection)
    putString(key, json)
}

/**
 * Retrieve a [T] [Collection] value from the preferences.
 * @author Arnau Mora
 * @param key The name of the preference to retrieve.
 * @param default The value to return if the preference doesn't exist, or is not valid.
 * @return Returns the preference value if it exists, or [default] if not.
 */
inline fun <reified T : Parcelable?> SharedPreferences.getParcelableList(
    key: String,
    default: List<T>
): List<T> {
    val json = getString(key, null)
    return try {
        if (json != null) {
            val result = arrayListOf<T>()
            for (e in Gson().fromJson(json, Collection::class.java))
                if (e is T)
                    result.add(e)
            result
        } else default
    } catch (_: JsonSyntaxException) {
        default
    }
}