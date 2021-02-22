package com.dengage.sdk.inappmessage

import android.app.Activity
import android.content.Context
import com.dengage.sdk.Logger
import com.dengage.sdk.Utils
import com.dengage.sdk.cache.Prefs
import com.dengage.sdk.inappmessage.model.InAppMessage
import com.dengage.sdk.inappmessage.utils.InAppMessageUtils
import com.dengage.sdk.models.DengageError
import com.dengage.sdk.models.Subscription
import com.dengage.sdk.service.NetworkRequest
import com.dengage.sdk.service.NetworkRequestCallback
import com.dengage.sdk.service.NetworkUrlUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

/**
 * Created by Batuhan Coskun on 26 December 2020
 */
class InAppMessageManager(
        private val context: Context,
        private val subscription: Subscription,
        private val logger: Logger
) {

    private val prefs: Prefs = Prefs(context)

    fun fetchInAppMessages() {
        // control in app message enabled
        val sdkParameters = prefs.sdkParameters
        if (sdkParameters?.accountName == null || sdkParameters.inAppEnabled == null ||
                !sdkParameters.inAppEnabled) {
            return
        }

        if (System.currentTimeMillis() >= prefs.inAppMessageFetchTime + 3600000) {
            val networkRequest = NetworkRequest(
                    NetworkUrlUtils.getInAppMessagesRequestUrl(context, sdkParameters.accountName, subscription),
                    Utils.getUserAgent(context), object : NetworkRequestCallback {
                override fun responseFetched(response: String?) {
                    val listType = object : TypeToken<MutableList<InAppMessage>>() {}.type
                    val fetchedInAppMessages = Gson().fromJson<MutableList<InAppMessage>>(response, listType)

                    prefs.inAppMessageFetchTime = System.currentTimeMillis()
                    prefs.inAppMessages = fetchedInAppMessages
                }

                override fun requestError(error: DengageError) {
                    prefs.inAppMessageFetchTime = System.currentTimeMillis() - 3600000
                }
            }, 5000)
            networkRequest.executeTask()
        }
    }

    fun setInAppMessageAsDisplayed(inAppMessageId: String) {
        // control in app message enabled
        val sdkParameters = prefs.sdkParameters
        if (sdkParameters?.accountName == null || sdkParameters.inAppEnabled == null ||
                !sdkParameters.inAppEnabled) {
            return
        }

        val networkRequest = NetworkRequest(
                NetworkUrlUtils.getInAppMessageAsDisplayedRequestUrl(context, inAppMessageId,
                        sdkParameters.accountName, subscription),
                Utils.getUserAgent(context), null, 5000)
        networkRequest.executeTask()
    }

    fun setInAppMessageAsClicked(inAppMessageId: String) {
        // control in app message enabled
        val sdkParameters = prefs.sdkParameters
        if (sdkParameters?.accountName == null || sdkParameters.inAppEnabled == null ||
                !sdkParameters.inAppEnabled) {
            return
        }

        val networkRequest = NetworkRequest(
                NetworkUrlUtils.getInAppMessageAsClickedRequestUrl(context, inAppMessageId,
                        sdkParameters.accountName, subscription),
                Utils.getUserAgent(context), null, 5000)
        networkRequest.executeTask()
    }

    fun setInAppMessageAsDismissed(inAppMessageId: String) {
        // control in app message enabled
        val sdkParameters = prefs.sdkParameters
        if (sdkParameters?.accountName == null || sdkParameters.inAppEnabled == null ||
                !sdkParameters.inAppEnabled) {
            return
        }

        val networkRequest = NetworkRequest(
                NetworkUrlUtils.getInAppMessageAsDismissedRequestUrl(context, inAppMessageId,
                        sdkParameters.accountName, subscription),
                Utils.getUserAgent(context), null, 5000)
        networkRequest.executeTask()
    }

    fun setNavigation(activity: Activity, screenName: String? = null, screenData: Map<String, Any>? = null) {
        addNavigation()
        val inAppMessages = InAppMessageUtils.findNotExpiredInAppMessages(logger, Date(), prefs.inAppMessages)
        prefs.inAppMessages = inAppMessages
        if (!inAppMessages.isNullOrEmpty()) {
            val priorInAppMessage = InAppMessageUtils.findPriorInAppMessage(inAppMessages)
            if (priorInAppMessage != null) {
                // todo show in app message
            }
        }
    }

    /**
    Starts new session for in app message navigation count controls
     */
    fun startNewSession() {
        prefs.sessionNavigationCount = 0
    }

    /**
    Add navigation to cache for in app message navigation count controls
     */
    private fun addNavigation() {
        prefs.sessionNavigationCount = prefs.sessionNavigationCount?.plus(1)
    }

}