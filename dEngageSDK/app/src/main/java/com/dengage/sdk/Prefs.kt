package com.dengage.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.dengage.sdk.models.InboxMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by Batuhan Coskun on 30 November 2020
 */
class Prefs(context: Context) {

    private val preferences = getSharedPreferences(context)

    companion object {
        const val INBOX_MESSAGES = "INBOX_MESSAGES"

        fun getSharedPreferences(context: Context): SharedPreferences =
                context.getSharedPreferences(Constants.DEN_DEVICE_UNIQUE_ID, Context.MODE_PRIVATE)
    }

    var inboxMessages: MutableList<InboxMessage>?
        get() = preferences.get(INBOX_MESSAGES)
        set(value) = preferences.set(INBOX_MESSAGES, value)

}

/**
 * Finds value on given key.
 *
 * [T] is the type of value
 *
 * @param key          The name of the preference.
 * @param defaultValue Optional default value - will take null for strings, false for bool and -1 for numeric values if [defaultValue] is not specified
 * @return The value associated with this key, defValue if there isn't any
 */
inline fun <reified T : Any> SharedPreferences.get(key: String, defaultValue: T? = null): T? {
    return when (T::class) {
        String::class -> getString(key, defaultValue as? String) as T?
        Int::class -> getInt(key, defaultValue as? Int ?: -1) as T?
        Boolean::class -> getBoolean(key, defaultValue as? Boolean ?: false) as T?
        Float::class -> getFloat(key, defaultValue as? Float ?: -1f) as T?
        Long::class -> getLong(key, defaultValue as? Long ?: -1) as T?
        else -> getString(key, null)?.let {
            Gson().fromJson(it, object : TypeToken<T>() {}.type)
        }
    }
}

/**
 * Puts a key value pair in shared prefs if doesn't exists, otherwise updates value on given [key].
 *
 * @param key   The name of the preference.
 * @param value The new set for the preference.
 */
fun SharedPreferences.set(key: String, value: Any?, immediately: Boolean = false) {
    when (value) {
        is String? -> edit(immediately) { it.putString(key, value) }
        is Int -> edit(immediately) { it.putInt(key, value) }
        is Boolean -> edit(immediately) { it.putBoolean(key, value) }
        is Float -> edit(immediately) { it.putFloat(key, value) }
        is Long -> edit(immediately) { it.putLong(key, value) }
        else -> edit(immediately) { it.putString(key, Gson().toJson(value)) }
    }
}

@SuppressLint("ApplySharedPref")
private fun SharedPreferences.edit(
        immediately: Boolean = false,
        operation: (SharedPreferences.Editor) -> Unit
) {
    val editor = this.edit()
    operation(editor)

    when (immediately) {
        true -> editor.commit()
        else -> editor.apply()
    }
}