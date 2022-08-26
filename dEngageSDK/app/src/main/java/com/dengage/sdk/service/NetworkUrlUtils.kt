package com.dengage.sdk.service

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.dengage.sdk.Constants
import com.dengage.sdk.Utils
import com.dengage.sdk.models.SdkParameters
import com.dengage.sdk.models.Subscription

/**
 * Created by Batuhan Coskun on 29 January 2021
 */
object NetworkUrlUtils {

    const val ACCOUNT = "acc"
    const val DEVICE_ID = "did"
    const val CD_KEY = "cdkey"
    const val TYPE = "type"
    const val LIMIT = "limit"
    const val OFFSET = "offset"
    const val MESSAGE_ID = "msgId"
    const val MESSAGE_DETAILS = "message_details"
    const val BUTTON_ID = "button_id"
    const val APPID = "appid"

    fun getSdkParametersRequestUrl(context: Context, integrationKey: String): String {
        var baseApiUri = Utils.getMetaData(context, "den_push_api_url")
        if (TextUtils.isEmpty(baseApiUri)) {
            baseApiUri = Constants.DEN_PUSH_API_URI
        }
        baseApiUri += "/api/getSdkParams"
        val uriWithQueryParams = Uri.parse(baseApiUri)
            .buildUpon()
            .appendQueryParameter("ik", integrationKey)
            .build()
        return uriWithQueryParams.toString()
    }

    fun getInboxMessagesRequestUrl(
        context: Context, accountName: String,
        subscription: Subscription,
        limit: Int, offset: Int
    ): String {
        var baseApiUri = Utils.getMetaData(context, "den_push_api_url")
        if (TextUtils.isEmpty(baseApiUri)) {
            baseApiUri = Constants.DEN_PUSH_API_URI
        }
        baseApiUri += "/api/pi/getMessages"
        val uriWithQueryParams = Uri.parse(baseApiUri)
            .buildUpon()
            .appendQueryParameter(ACCOUNT, accountName)
            .appendQueryParameter(
                CD_KEY, if (TextUtils.isEmpty(subscription.contactKey)) subscription.deviceId
                else subscription.contactKey
            )
            .appendQueryParameter(DEVICE_ID, subscription.deviceId)
            .appendQueryParameter(TYPE, if (TextUtils.isEmpty(subscription.contactKey)) "d" else "c")
            .appendQueryParameter(LIMIT, limit.toString())
            .appendQueryParameter(OFFSET, offset.toString())
            .build()
        return uriWithQueryParams.toString()
    }

    fun setInboxMessageAsDeletedRequestUrl(
        context: Context, messageId: String,
        accountName: String, subscription: Subscription
    ): String {
        var baseApiUri = Utils.getMetaData(context, "den_push_api_url")
        if (TextUtils.isEmpty(baseApiUri)) {
            baseApiUri = Constants.DEN_PUSH_API_URI
        }
        baseApiUri += "/api/pi/setAsDeleted"
        val uriWithQueryParams = Uri.parse(baseApiUri)
            .buildUpon()
            .appendQueryParameter(ACCOUNT, accountName)
            .appendQueryParameter(
                CD_KEY, if (TextUtils.isEmpty(subscription.contactKey)) subscription.deviceId
                else subscription.contactKey
            )
            .appendQueryParameter(DEVICE_ID, subscription.deviceId)
            .appendQueryParameter(TYPE, if (TextUtils.isEmpty(subscription.contactKey)) "d" else "c")
            .appendQueryParameter(MESSAGE_ID, messageId)
            .build()
        return uriWithQueryParams.toString()
    }

    fun setInboxMessageAsClickedRequestUrl(
        context: Context, messageId: String,
        accountName: String, subscription: Subscription
    ): String {
        var baseApiUri = Utils.getMetaData(context, "den_push_api_url")
        if (TextUtils.isEmpty(baseApiUri)) {
            baseApiUri = Constants.DEN_PUSH_API_URI
        }
        baseApiUri += "/api/pi/setAsClicked"
        val uriWithQueryParams = Uri.parse(baseApiUri)
            .buildUpon()
            .appendQueryParameter(ACCOUNT, accountName)
            .appendQueryParameter(
                CD_KEY, if (TextUtils.isEmpty(subscription.contactKey)) subscription.deviceId
                else subscription.contactKey
            )
            .appendQueryParameter(DEVICE_ID, subscription.deviceId)
            .appendQueryParameter(TYPE, if (TextUtils.isEmpty(subscription.contactKey)) "d" else "c")
            .appendQueryParameter(MESSAGE_ID, messageId)
            .build()
        return uriWithQueryParams.toString()
    }

    fun getInAppMessagesRequestUrl(
        context: Context, accountName: String,
        subscription: Subscription,
        sdkParameters: SdkParameters
    ): String {
        var baseApiUri = Utils.getMetaData(context, "den_push_api_url")
        if (TextUtils.isEmpty(baseApiUri)) {
            baseApiUri = Constants.DEN_PUSH_API_URI
        }
        baseApiUri += "/api/inapp/getMessages"
        val uriWithQueryParams = Uri.parse(baseApiUri)
            .buildUpon()
            .appendQueryParameter(ACCOUNT, accountName)
            .appendQueryParameter(
                CD_KEY, if (TextUtils.isEmpty(subscription.contactKey)) subscription.deviceId
                else subscription.contactKey
            )
            .appendQueryParameter(DEVICE_ID, subscription.deviceId)
            .appendQueryParameter(
                TYPE,
                if (TextUtils.isEmpty(subscription.contactKey)) "d" else "c"
            )
            .appendQueryParameter(APPID, sdkParameters.appId)

            .build()
        return uriWithQueryParams.toString()
    }

    fun getInAppMessageAsDisplayedRequestUrl(
        context: Context, inAppMessageDetails: String?,
        accountName: String, subscription: Subscription
    ): String {
        var baseApiUri = Utils.getMetaData(context, "den_push_api_url")
        if (TextUtils.isEmpty(baseApiUri)) {
            baseApiUri = Constants.DEN_PUSH_API_URI
        }
        baseApiUri += "/api/inapp/setAsDisplayed"
        val uriWithQueryParams = Uri.parse(baseApiUri)
            .buildUpon()
            .appendQueryParameter(ACCOUNT, accountName)
            .appendQueryParameter(
                CD_KEY, if (TextUtils.isEmpty(subscription.contactKey)) subscription.deviceId
                else subscription.contactKey
            )
            .appendQueryParameter(DEVICE_ID, subscription.deviceId)
            .appendQueryParameter(TYPE, if (TextUtils.isEmpty(subscription.contactKey)) "d" else "c")
            .appendQueryParameter(MESSAGE_DETAILS, inAppMessageDetails)
            .build()
        return uriWithQueryParams.toString()
    }

    fun getInAppMessageAsClickedRequestUrl(
        context: Context, inAppMessageDetails: String?,
        buttonId: String?, accountName: String, subscription: Subscription
    ): String {
        var baseApiUri = Utils.getMetaData(context, "den_push_api_url")
        if (TextUtils.isEmpty(baseApiUri)) {
            baseApiUri = Constants.DEN_PUSH_API_URI
        }
        baseApiUri += "/api/inapp/setAsClicked"
        val uriWithQueryParams = Uri.parse(baseApiUri)
            .buildUpon()
            .appendQueryParameter(ACCOUNT, accountName)
            .appendQueryParameter(
                CD_KEY, if (TextUtils.isEmpty(subscription.contactKey)) subscription.deviceId
                else subscription.contactKey
            )
            .appendQueryParameter(DEVICE_ID, subscription.deviceId)
            .appendQueryParameter(TYPE, if (TextUtils.isEmpty(subscription.contactKey)) "d" else "c")
            .appendQueryParameter(MESSAGE_DETAILS, inAppMessageDetails)

        if (buttonId != null) {
            uriWithQueryParams.appendQueryParameter(BUTTON_ID, buttonId)
        }
        return uriWithQueryParams.build().toString()
    }

    fun getInAppMessageAsDismissedRequestUrl(
        context: Context, inAppMessageDetails: String?,
        accountName: String, subscription: Subscription
    ): String {
        var baseApiUri = Utils.getMetaData(context, "den_push_api_url")
        if (TextUtils.isEmpty(baseApiUri)) {
            baseApiUri = Constants.DEN_PUSH_API_URI
        }
        baseApiUri += "/api/inapp/setAsDismissed"
        val uriWithQueryParams = Uri.parse(baseApiUri)
            .buildUpon()
            .appendQueryParameter(ACCOUNT, accountName)
            .appendQueryParameter(
                CD_KEY, if (TextUtils.isEmpty(subscription.contactKey)) subscription.deviceId
                else subscription.contactKey
            )
            .appendQueryParameter(DEVICE_ID, subscription.deviceId)
            .appendQueryParameter(TYPE, if (TextUtils.isEmpty(subscription.contactKey)) "d" else "c")
            .appendQueryParameter(MESSAGE_DETAILS, inAppMessageDetails)
            .build()
        return uriWithQueryParams.toString()
    }

    fun setTagsRequestUrl(context: Context): String {
        var baseApiUri = Utils.getMetaData(context, "den_push_api_url")
        if (TextUtils.isEmpty(baseApiUri)) {
            baseApiUri = Constants.DEN_PUSH_API_URI
        }
        baseApiUri += "/api/setTags"
        val uriWithQueryParams = Uri.parse(baseApiUri)
            .buildUpon()
            .build()
        return uriWithQueryParams.toString()
    }

}