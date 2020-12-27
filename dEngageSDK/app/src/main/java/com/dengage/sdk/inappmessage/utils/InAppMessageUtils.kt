package com.dengage.sdk.inappmessage.utils

import com.dengage.sdk.Constants
import com.dengage.sdk.Logger
import com.dengage.sdk.inappmessage.model.InAppMessage
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Batuhan Coskun on 27 December 2020
 */
object InAppMessageUtils {

    /**
     * Find not expired in app messages with controlling expire date and date now
     *
     * @param inAppMessages in app messages that will be filtered with expire date
     */
    fun findNotExpiredInAppMessages(logger: Logger, untilDate: Date, inAppMessages: List<InAppMessage>?): MutableList<InAppMessage>? {
        if (inAppMessages == null) return null
        val notExpiredMessages = mutableListOf<InAppMessage>()
        val expireDateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
        for (inAppMessage in inAppMessages) {
            try {
                val expireDate = expireDateFormat.parse(inAppMessage.expireDate)
                if (untilDate.before(expireDate)) {
                    notExpiredMessages.add(inAppMessage)
                }
            } catch (e: ParseException) {
                logger.Error("removeExpiredInAppMessages: " + e.message)
                e.printStackTrace()
            }
        }
        return notExpiredMessages
    }

    /**
     * Find prior in app message to show with respect to hasShown and expireDate parameters
     */
    fun findPriorInAppMessage(inAppMessages: List<InAppMessage>): InAppMessage? {
        // sort list with comparator then return first
        return inAppMessages.sortedWith(InAppMessageComparator()).firstOrNull()
    }

}