package com.dengage.sdk.cache

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.dengage.sdk.Constants
import com.dengage.sdk.Utils
import com.dengage.sdk.inappmessage.model.InAppMessage
import com.dengage.sdk.models.SdkParameters
import java.util.*

/**
 * Created by Batuhan Coskun on 30 November 2020
 */
class Prefs(context: Context) {

    private val preferences = getSharedPreferences(context)

    companion object {
        const val IN_APP_MESSAGES = "IN_APP_MESSAGES"
        const val SDK_PARAMETERS = "SDK_PARAMETERS"
        const val IN_APP_MESSAGE_FETCH_TIME = "IN_APP_MESSAGE_FETCH_TIME"
        const val APP_TRACKING_TIME = "APP_TRACKING_TIME"
        const val IN_APP_MESSAGE_SHOW_TIME = "IN_APP_MESSAGE_SHOW_TIME"
        const val NOTIFICATION_CHANNEL_NAME = "NOTIFICATION_CHANNEL_NAME"
        const val APP_SESSION_TIME = "APP_SESSION_TIME"
        const val APP_SESSION_ID = "APP_SESSION_ID"

        fun getSharedPreferences(context: Context): SharedPreferences =
            context.getSharedPreferences(Constants.DEN_DEVICE_UNIQUE_ID, Context.MODE_PRIVATE)
    }

    var sdkParameters: SdkParameters?
        get() = preferences.get(SDK_PARAMETERS)
        set(value) = preferences.set(SDK_PARAMETERS, value)

    var appTrackingTime: Long
        get() = preferences.get(APP_TRACKING_TIME, 0) ?: 0
        set(value) = preferences.set(APP_TRACKING_TIME, value)

    var inAppMessages: MutableList<InAppMessage>?
        get() = preferences.get(IN_APP_MESSAGES)
        set(value) = preferences.set(IN_APP_MESSAGES, value)

    var inAppMessageFetchTime: Long
        get() = preferences.get(IN_APP_MESSAGE_FETCH_TIME, 0) ?: 0
        set(value) = preferences.set(IN_APP_MESSAGE_FETCH_TIME, value)

    var inAppMessageShowTime: Long
        get() = preferences.get(IN_APP_MESSAGE_SHOW_TIME, 0) ?: 0
        set(value) = preferences.set(IN_APP_MESSAGE_SHOW_TIME, value)

    var notificationChannelName: String
        get() = preferences.get(NOTIFICATION_CHANNEL_NAME, Constants.CHANNEL_NAME) ?: Constants.CHANNEL_NAME
        set(value) = preferences.set(NOTIFICATION_CHANNEL_NAME, value)

    var appSessionTime: Long
        get() = preferences.get(APP_SESSION_TIME, 0) ?: 0
        set(value) = preferences.set(APP_SESSION_TIME, value)

    var appSessionId: String
        get() = preferences.get(APP_SESSION_ID, Utils.generateSessionId()) ?: ""
        set(value) = preferences.set(APP_SESSION_ID, value)


    fun clear() {
        preferences.edit().clear().apply()
    }

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
            GsonHolder.fromJson<T>(it)
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
        else -> edit(immediately) { it.putString(key, GsonHolder.toJson(value)) }
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
