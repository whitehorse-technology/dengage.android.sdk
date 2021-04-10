package com.dengage.sdk.inappmessage

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.dengage.sdk.Logger
import com.dengage.sdk.NotificationReceiver
import com.dengage.sdk.Utils
import com.dengage.sdk.cache.GsonHolder
import com.dengage.sdk.cache.Prefs
import com.dengage.sdk.inappmessage.model.*
import com.dengage.sdk.inappmessage.utils.InAppMessageUtils
import com.dengage.sdk.models.DengageError
import com.dengage.sdk.models.Subscription
import com.dengage.sdk.service.NetworkRequest
import com.dengage.sdk.service.NetworkRequestCallback
import com.dengage.sdk.service.NetworkUrlUtils
import com.google.gson.reflect.TypeToken
import java.util.*

/**
 * Created by Batuhan Coskun on 26 December 2020
 */
class InAppMessageManager(
        private val context: Context,
        private var subscription: Subscription,
        private val logger: Logger
) : InAppMessageDialog.InAppMessageCallback {

    private val prefs: Prefs = Prefs(context)

    /**
     * Call this function if subscription model is changed
     */
    fun updateSubscription(subscription: Subscription) {
        this.subscription = subscription
    }

    /**
     * Call this method for the pages that you should show in app message if available
     */
    fun setNavigation(activity: AppCompatActivity, screenName: String? = null) {
        // control next in app message show time
        if (System.currentTimeMillis() < prefs.inAppMessageShowTime) return

        val inAppMessages =
                InAppMessageUtils.findNotExpiredInAppMessages(logger, Date(), prefs.inAppMessages)
        prefs.inAppMessages = inAppMessages
        if (!inAppMessages.isNullOrEmpty()) {
            val priorInAppMessage =
                    InAppMessageUtils.findPriorInAppMessage(inAppMessages, screenName)
            if (priorInAppMessage != null) {
                showInAppMessage(activity, priorInAppMessage)
            }
        }
    }

    /**
     * Fetch in app messages if enabled and fetch time is available
     */
    fun fetchInAppMessages() {
        // control in app message enabled
        val sdkParameters = prefs.sdkParameters
        if (sdkParameters?.accountName == null || sdkParameters.inAppEnabled == null ||
                !sdkParameters.inAppEnabled
        ) {
            return
        }

        // control next in app message fetch time
        if (System.currentTimeMillis() < prefs.inAppMessageFetchTime) return

        val nextFetchTimePlus = (sdkParameters.inAppFetchIntervalInMin ?: 0) * 60000
        prefs.inAppMessageFetchTime = System.currentTimeMillis() + nextFetchTimePlus
        val networkRequest = NetworkRequest(
                NetworkUrlUtils.getInAppMessagesRequestUrl(
                        context,
                        sdkParameters.accountName,
                        subscription
                ),
                Utils.getUserAgent(context), object : NetworkRequestCallback {
            override fun responseFetched(response: String?) {
                logger.Error("in app messages response fetched: $response")

                val listType = object : TypeToken<MutableList<InAppMessage>>() {}.type
                val fetchedInAppMessages = try {
                    GsonHolder.gson.fromJson<MutableList<InAppMessage>>(response, listType)
                } catch (e: Exception) {
                    logger.Error("in app messages response error: ${e.message}")
                    null
                }

                if (!fetchedInAppMessages.isNullOrEmpty()) {
                    // get existing in app messages and save with fetched in app messages
                    var existingInAppMessages = prefs.inAppMessages
                    if (existingInAppMessages == null) {
                        existingInAppMessages = mutableListOf()
                    }
                    existingInAppMessages.addAll(fetchedInAppMessages)

                    prefs.inAppMessages = existingInAppMessages
                }
            }

            override fun requestError(error: DengageError) {
                logger.Error("in app messages request error: ${error.errorMessage}")
                prefs.inAppMessageFetchTime = System.currentTimeMillis()
            }
        }, 5000
        )
        networkRequest.executeTask()
    }

    /**
     * Call service for setting in app message as displayed
     */
    private fun setInAppMessageAsDisplayed(inAppMessageDetails: String?) {
        // control in app message enabled
        val sdkParameters = prefs.sdkParameters
        if (sdkParameters?.accountName == null || sdkParameters.inAppEnabled == null ||
                !sdkParameters.inAppEnabled
        ) {
            return
        }

        val networkRequest = NetworkRequest(
                NetworkUrlUtils.getInAppMessageAsDisplayedRequestUrl(
                        context, inAppMessageDetails,
                        sdkParameters.accountName, subscription
                ),
                Utils.getUserAgent(context), null, 5000
        )
        networkRequest.executeTask()
    }

    /**
     * Call service for setting in app message as clicked
     */
    private fun setInAppMessageAsClicked(inAppMessageId: String, inAppMessageDetails: String?) {
        // control in app message enabled
        val sdkParameters = prefs.sdkParameters
        if (sdkParameters?.accountName == null || sdkParameters.inAppEnabled == null ||
                !sdkParameters.inAppEnabled
        ) {
            return
        }

        // remove in app message from cache if clicked
        removeInAppMessageFromCache(inAppMessageId)

        val networkRequest = NetworkRequest(
                NetworkUrlUtils.getInAppMessageAsClickedRequestUrl(
                        context, inAppMessageDetails,
                        sdkParameters.accountName, subscription
                ),
                Utils.getUserAgent(context), null, 5000
        )
        networkRequest.executeTask()
    }

    /**
     * Call service for setting in app message as dismissed
     */
    private fun setInAppMessageAsDismissed(inAppMessageDetails: String?) {
        // control in app message enabled
        val sdkParameters = prefs.sdkParameters
        if (sdkParameters?.accountName == null || sdkParameters.inAppEnabled == null ||
                !sdkParameters.inAppEnabled
        ) {
            return
        }

        val networkRequest = NetworkRequest(
                NetworkUrlUtils.getInAppMessageAsDismissedRequestUrl(
                        context, inAppMessageDetails,
                        sdkParameters.accountName, subscription
                ),
                Utils.getUserAgent(context), null, 5000
        )
        networkRequest.executeTask()
    }

    /**
     * Show in app message dialog on activity screen
     */
    private fun showInAppMessage(activity: AppCompatActivity, inAppMessage: InAppMessage) {
        setInAppMessageAsDisplayed(
                inAppMessageDetails = inAppMessage.data.messageDetails
        )

        if (inAppMessage.data.displayTiming.showEveryXMinutes != null &&
                inAppMessage.data.displayTiming.showEveryXMinutes != 0) {
            inAppMessage.data.nextDisplayTime =
                    inAppMessage.data.displayTiming.showEveryXMinutes * 60000L
            updateInAppMessageOnCache(inAppMessage)
        } else {
            removeInAppMessageFromCache(inAppMessageId = inAppMessage.id)
        }

        // update next in app message show time
        prefs.inAppMessageShowTime = System.currentTimeMillis() +
                ((prefs.sdkParameters?.inAppMinSecBetweenMessages ?: 0) * 1000)

        // set delay for showing in app message
        val delay = (inAppMessage.data.displayTiming.delay ?: 0) * 1000L
        Timer().schedule(object : TimerTask() {
            override fun run() {
                activity.runOnUiThread {
                    val inAppMessageDialog = InAppMessageDialog.newInstance(inAppMessage)
                    inAppMessageDialog.setInAppMessageCallback(this@InAppMessageManager)
                    inAppMessageDialog.show(
                            activity.supportFragmentManager,
                            InAppMessageDialog::class.java.simpleName
                    )
                }
            }
        }, delay)
    }

    private fun updateInAppMessageOnCache(inAppMessage: InAppMessage) {
        val inAppMessages = prefs.inAppMessages
        inAppMessages?.removeAll { message -> message.id == inAppMessage.id }
        inAppMessages?.add(inAppMessage)
        prefs.inAppMessages = inAppMessages
    }

    private fun removeInAppMessageFromCache(inAppMessageId: String) {
        val inAppMessages = prefs.inAppMessages
        inAppMessages?.removeAll { inAppMessage -> inAppMessage.id == inAppMessageId }
        prefs.inAppMessages = inAppMessages
    }

    override fun inAppMessageClicked(inAppMessage: InAppMessage) {
        setInAppMessageAsClicked(
                inAppMessageId = inAppMessage.id,
                inAppMessageDetails = inAppMessage.data.messageDetails
        )
        NotificationReceiver.launchActivity(
                context,
                null,
                inAppMessage.data.content.params.targetUrl
        )
    }

    override fun inAppMessageDismissed(inAppMessage: InAppMessage) {
        setInAppMessageAsDismissed(
                inAppMessageDetails = inAppMessage.data.messageDetails
        )
    }

}