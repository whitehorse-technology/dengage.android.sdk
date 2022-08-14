package com.dengage.sdk.data.cache

import android.content.Context
import android.content.SharedPreferences
import com.dengage.sdk.domain.geofence.model.GeofenceHistory
import com.dengage.sdk.inappmessage.model.InAppMessage
import com.dengage.sdk.models.SdkParameters
import com.dengage.sdk.models.Subscription
import com.dengage.sdk.rfm.model.RFMScore
import com.dengage.sdk.util.Constants
import com.dengage.sdk.util.ContextHolder
import com.dengage.sdk.util.DengageUtils

object Prefs {

     val preferences: SharedPreferences by lazy {
        ContextHolder.context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE)
    }

     var installationId: String?
        get() = preferences.get(PreferenceKey.___DEN_DEVICE_UNIQUE_ID___)
        set(value) = preferences.set(PreferenceKey.___DEN_DEVICE_UNIQUE_ID___, value)

     var pushApiBaseUrl: String
        get() = preferences.get(PreferenceKey.PUSH_API_BASE_URL) ?: Constants.PUSH_API_URI
        set(value) = preferences.set(PreferenceKey.PUSH_API_BASE_URL, value)

     var eventApiBaseUrl: String
        get() = preferences.get(PreferenceKey.EVENT_API_BASE_URL) ?: Constants.EVENT_API_URI
        set(value) = preferences.set(PreferenceKey.EVENT_API_BASE_URL, value)

     var sdkParameters: SdkParameters?
        get() = preferences.get(PreferenceKey.SDK_PARAMETERS)
        set(value) = preferences.set(PreferenceKey.SDK_PARAMETERS, value)

     var appTrackingTime: Long
        get() = preferences.get(PreferenceKey.APP_TRACKING_TIME, 0) ?: 0
        set(value) = preferences.set(PreferenceKey.APP_TRACKING_TIME, value)

     var inAppMessages: MutableList<InAppMessage>?
        get() = preferences.get(PreferenceKey.IN_APP_MESSAGES)
        set(value) = preferences.set(PreferenceKey.IN_APP_MESSAGES, value)

     var inAppMessageFetchTime: Long
        get() = preferences.get(PreferenceKey.IN_APP_MESSAGE_FETCH_TIME, 0) ?: 0
        set(value) = preferences.set(PreferenceKey.IN_APP_MESSAGE_FETCH_TIME, value)

     var inAppMessageShowTime: Long
        get() = preferences.get(PreferenceKey.IN_APP_MESSAGE_SHOW_TIME, 0) ?: 0
        set(value) = preferences.set(PreferenceKey.IN_APP_MESSAGE_SHOW_TIME, value)

     var notificationChannelName: String
        get() = preferences.get(PreferenceKey.NOTIFICATION_CHANNEL_NAME, Constants.NOTIFICATION_CHANNEL_NAME)
            ?: Constants.NOTIFICATION_CHANNEL_NAME
        set(value) = preferences.set(PreferenceKey.NOTIFICATION_CHANNEL_NAME, value)

     var subscription: Subscription?
        get() = preferences.get(PreferenceKey.SUBSCRIPTION)
        set(value) = preferences.set(PreferenceKey.SUBSCRIPTION, value)

     var inboxMessageFetchTime: Long
        get() = preferences.get(PreferenceKey.INBOX_MESSAGE_FETCH_TIME, 0) ?: 0
        set(value) = preferences.set(PreferenceKey.INBOX_MESSAGE_FETCH_TIME, value)

     var appSessionTime: Long
        get() = preferences.get(PreferenceKey.APP_SESSION_TIME, 0) ?: 0
        set(value) = preferences.set(PreferenceKey.APP_SESSION_TIME, value)

     var appSessionId: String
        get() = preferences.get(PreferenceKey.APP_SESSION_ID, DengageUtils.generateUUID()) ?: ""
        set(value) = preferences.set(PreferenceKey.APP_SESSION_ID, value)

     var logVisibility: Boolean
        get() = preferences.get(PreferenceKey.LOG_VISIBILITY, true) ?: true
        set(value) = preferences.set(PreferenceKey.LOG_VISIBILITY, value)

     var rfmScores: MutableList<RFMScore>?
        get() = preferences.get(PreferenceKey.RFM_SCORES)
        set(value) = preferences.set(PreferenceKey.RFM_SCORES, value)

     var geofenceApiBaseUrl: String
        get() = preferences.get(PreferenceKey.GEOFENCE_API_BASE_URL) ?: Constants.GEOFENCE_API_URI
        set(value) = preferences.set(PreferenceKey.GEOFENCE_API_BASE_URL, value)

     var geofenceEnabled: Boolean
        get() = preferences.get(PreferenceKey.GEOFENCE_ENABLED, defaultValue = false) ?:false
        set(value) = preferences.set(PreferenceKey.GEOFENCE_ENABLED, value)

     var geofenceHistory: GeofenceHistory
        get() = preferences.get(PreferenceKey.GEOFENCE_HISTORY, defaultValue = GeofenceHistory()) ?: GeofenceHistory()
        set(value) = preferences.set(PreferenceKey.GEOFENCE_HISTORY, value)

     var geofencePermissionsDenied: Boolean
        get() = preferences.get(PreferenceKey.GEOFENCE_PERMISSIONS_DENIED, defaultValue = false) ?:false
        set(value) = preferences.set(PreferenceKey.GEOFENCE_PERMISSIONS_DENIED, value)

    fun clear() {
        preferences.edit().clear().apply()
    }
}