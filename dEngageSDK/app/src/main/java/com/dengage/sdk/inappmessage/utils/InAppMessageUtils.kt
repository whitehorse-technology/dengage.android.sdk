package com.dengage.sdk.inappmessage.utils

import com.dengage.sdk.Constants
import com.dengage.sdk.Logger
import com.dengage.sdk.inappmessage.model.InAppMessage
import com.dengage.sdk.inappmessage.model.Operator
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
    fun findNotExpiredInAppMessages(logger: Logger?, untilDate: Date, inAppMessages: List<InAppMessage>?): MutableList<InAppMessage>? {
        if (inAppMessages == null) return null
        val notExpiredMessages = mutableListOf<InAppMessage>()
        val expireDateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
        for (inAppMessage in inAppMessages) {
            try {
                val expireDate = expireDateFormat.parse(inAppMessage.data.expireDate)
                if (untilDate.before(expireDate)) {
                    notExpiredMessages.add(inAppMessage)
                }
            } catch (e: ParseException) {
                logger?.Error("removeExpiredInAppMessages: " + e.message)
                e.printStackTrace()
            }
        }
        return notExpiredMessages
    }

    /**
     * Find prior in app message to show with respect to priority and expireDate parameters
     */
    fun findPriorInAppMessage(inAppMessages: List<InAppMessage>, screenName: String? = null): InAppMessage? {
        // sort list with comparator
        val sortedInAppMessages = inAppMessages.sortedWith(InAppMessageComparator())

        // if screen name is empty, find in app message that has no screen name filter
        // if screen name is not empty, find in app message that screen name filter has screen name value
        // Also control nextDisplayTime for showEveryXMinutes type in app messages
        return if (screenName.isNullOrEmpty()) {
            sortedInAppMessages.firstOrNull { inAppMessage: InAppMessage ->
                inAppMessage.data.displayCondition.screenNameFilters == null &&
                        isDisplayTimeAvailable(inAppMessage)

            }
        } else {
            sortedInAppMessages.firstOrNull { inAppMessage: InAppMessage ->
                inAppMessage.data.displayCondition.screenNameFilters?.firstOrNull { screenNameFilter ->
                    operateScreenValues(screenNameFilter.value, screenName, screenNameFilter.operator)
                } != null && isDisplayTimeAvailable(inAppMessage)
            }
        }
    }

    fun operateScreenValues(screenNameFilterValue: String, screenName: String, operator: String): Boolean {
        when (operator) {
            Operator.EQUALS.operator -> {
                return screenNameFilterValue == screenName
            }
            Operator.NOT_EQUALS.operator -> {
                return screenNameFilterValue != screenName
            }
            Operator.LIKE.operator -> {
                return screenName.contains(screenNameFilterValue, true)
            }
            Operator.NOT_LIKE.operator -> {
                return !screenName.contains(screenNameFilterValue, true)
            }
            Operator.STARTS_WITH.operator -> {
                return screenName.startsWith(screenNameFilterValue, true)
            }
            Operator.NOT_STARTS_WITH.operator -> {
                return !screenName.startsWith(screenNameFilterValue, true)
            }
            Operator.ENDS_WITH.operator -> {
                return screenName.endsWith(screenNameFilterValue, true)
            }
            Operator.NOT_ENDS_WITH.operator -> {
                return !screenName.endsWith(screenNameFilterValue, true)
            }
            Operator.IN.operator -> {
                val screenNameFilterValues = screenNameFilterValue.split("|")
                return screenNameFilterValues.contains(screenName)
            }
            Operator.NOT_IN.operator -> {
                val screenNameFilterValues = screenNameFilterValue.split("|")
                return !screenNameFilterValues.contains(screenName)
            }
        }
        return true
    }

    private fun isDisplayTimeAvailable(inAppMessage: InAppMessage): Boolean {
        return (inAppMessage.data.displayTiming.showEveryXMinutes == null ||
                inAppMessage.data.nextDisplayTime <= System.currentTimeMillis())
    }

}