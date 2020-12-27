package com.dengage.sdk.inappmessage

import android.app.Activity
import com.dengage.sdk.Constants
import com.dengage.sdk.Logger
import com.dengage.sdk.Prefs
import com.dengage.sdk.inappmessage.model.InAppMessage
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Batuhan Coskun on 26 December 2020
 */
class InAppMessageManager(private val prefs: Prefs,
                          private val logger: Logger) {

    fun setNavigation(activity: Activity, screenName: String? = null, screenData: Map<String, Any>? = null) {
        addNavigation()
        val inAppMessages = findNotExpiredInAppMessages(prefs.inAppMessages)
        prefs.inAppMessages = inAppMessages
        if (!inAppMessages.isNullOrEmpty()) {
            // todo find prior in app message and show
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

    /**
     * Find not expired in app messages with controlling expire date and date now
     *
     * @param inAppMessages in app messages that will be filtered with expire date
     */
    private fun findNotExpiredInAppMessages(inAppMessages: List<InAppMessage>?): MutableList<InAppMessage>? {
        if (inAppMessages == null) return null
        val notExpiredMessages = mutableListOf<InAppMessage>()
        val dateNow = Date()
        val expireDateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
        for (inAppMessage in inAppMessages) {
            try {
                val expireDate = expireDateFormat.parse(inAppMessage.expireDate)
                if (dateNow.before(expireDate)) {
                    notExpiredMessages.add(inAppMessage)
                }
            } catch (e: ParseException) {
                logger.Error("removeExpiredInAppMessages: " + e.message)
                e.printStackTrace()
            }
        }
        return notExpiredMessages
    }

}