package com.dengage.sdk.inappmessage

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
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
) : InAppMessageDialog.InAppMessageCallback {

    private val prefs: Prefs = Prefs(context)

    /**
     *Call this method for the pages that you should show in app message if available
     */
    fun setNavigation(activity: AppCompatActivity, screenName: String? = null, screenData: Map<String, Any>? = null) {
        addNavigation()
        val inAppMessages = InAppMessageUtils.findNotExpiredInAppMessages(logger, Date(), prefs.inAppMessages)
        prefs.inAppMessages = inAppMessages
        if (!inAppMessages.isNullOrEmpty()) {
            val priorInAppMessage = InAppMessageUtils.findPriorInAppMessage(inAppMessages, screenName)
            if (priorInAppMessage != null) {
                showInAppMessage(activity, priorInAppMessage)
            }
        }
    }

    /**
     *Starts new session for in app message navigation count controls
     */
    fun startNewSession() {
        prefs.sessionNavigationCount = 0
    }

    /**
     * Add navigation to cache for in app message navigation count controls
     */
    private fun addNavigation() {
        prefs.sessionNavigationCount = prefs.sessionNavigationCount?.plus(1)
    }

    /**
     * Fetch in app messages if enabled and fetch time is available
     */
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

    /**
     * Call service for setting in app message as displayed
     */
    private fun setInAppMessageAsDisplayed(inAppMessageId: String) {
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

    /**
     * Call service for setting in app message as clicked
     */
    private fun setInAppMessageAsClicked(inAppMessageId: String) {
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

    /**
     * Call service for setting in app message as dismissed
     */
    private fun setInAppMessageAsDismissed(inAppMessageId: String) {
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

    /**
     * Show in app message dialog on activity screen
     */
    private fun showInAppMessage(activity: AppCompatActivity, inAppMessage: InAppMessage) {
        setInAppMessageAsDisplayed(inAppMessageId = inAppMessage.id)
        InAppMessageDialog.newInstance(inAppMessage)
                .show(activity.supportFragmentManager, InAppMessageDialog::class.java.simpleName)
    }

    override fun inAppMessageClicked(inAppMessage: InAppMessage) {
        setInAppMessageAsClicked(inAppMessageId = inAppMessage.id)
        // todo action in app message click
    }

    override fun inAppMessageDismissed(inAppMessage: InAppMessage) {
        setInAppMessageAsDismissed(inAppMessageId = inAppMessage.id)
    }

}