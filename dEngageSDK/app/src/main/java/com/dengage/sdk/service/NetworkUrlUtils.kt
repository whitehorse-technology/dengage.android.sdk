package com.dengage.sdk.service

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.dengage.sdk.Constants
import com.dengage.sdk.Utils
import com.dengage.sdk.models.Subscription

/**
 * Created by Batuhan Coskun on 29 January 2021
 */
object NetworkUrlUtils {

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

    fun getInboxMessagesRequestUrl(context: Context, accountName: String,
                                   subscription: Subscription,
                                   limit: Int, offset: Int): String {
        var baseApiUri = Utils.getMetaData(context, "den_push_api_url")
        if (TextUtils.isEmpty(baseApiUri)) {
            baseApiUri = Constants.DEN_PUSH_API_URI
        }
        baseApiUri += "/api/pi/getMessages"
        val uriWithQueryParams = Uri.parse(baseApiUri)
                .buildUpon()
                .appendQueryParameter("acc", accountName)
                .appendQueryParameter("cdkey", if (TextUtils.isEmpty(subscription.contactKey)) subscription.deviceId
                else subscription.contactKey)
                .appendQueryParameter("limit", limit.toString())
                .appendQueryParameter("offset", offset.toString())
                .build()
        return uriWithQueryParams.toString()
    }

    fun setAsDeletedRequestUrl(context: Context, messageId: String,
                               accountName: String, subscription: Subscription): String {
        var baseApiUri = Utils.getMetaData(context, "den_push_api_url")
        if (TextUtils.isEmpty(baseApiUri)) {
            baseApiUri = Constants.DEN_PUSH_API_URI
        }
        baseApiUri += "/api/pi/setAsDeleted"
        val uriWithQueryParams = Uri.parse(baseApiUri)
                .buildUpon()
                .appendQueryParameter("acc", accountName)
                .appendQueryParameter("cdkey", if (TextUtils.isEmpty(subscription.contactKey)) subscription.deviceId
                else subscription.contactKey)
                .appendQueryParameter("msgId", messageId)
                .build()
        return uriWithQueryParams.toString()
    }

    fun setAsClickedRequestUrl(context: Context, messageId: String,
                               accountName: String, subscription: Subscription): String {
        var baseApiUri = Utils.getMetaData(context, "den_push_api_url")
        if (TextUtils.isEmpty(baseApiUri)) {
            baseApiUri = Constants.DEN_PUSH_API_URI
        }
        baseApiUri += "/api/pi/setAsClicked"
        val uriWithQueryParams = Uri.parse(baseApiUri)
                .buildUpon()
                .appendQueryParameter("acc", accountName)
                .appendQueryParameter("cdkey", if (TextUtils.isEmpty(subscription.contactKey)) subscription.deviceId
                else subscription.contactKey)
                .appendQueryParameter("msgId", messageId)
                .build()
        return uriWithQueryParams.toString()
    }

}