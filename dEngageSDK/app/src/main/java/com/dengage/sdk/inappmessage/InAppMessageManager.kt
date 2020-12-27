package com.dengage.sdk.inappmessage

import android.app.Activity
import com.dengage.sdk.Logger
import com.dengage.sdk.Prefs
import com.dengage.sdk.inappmessage.utils.InAppMessageUtils
import java.util.*

/**
 * Created by Batuhan Coskun on 26 December 2020
 */
class InAppMessageManager(private val prefs: Prefs,
                          private val logger: Logger) {

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