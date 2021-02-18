package com.dengage.sdk.inappmessage.utils

import com.dengage.sdk.Constants
import com.dengage.sdk.inappmessage.model.InAppMessage
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Batuhan Coskun on 27 December 2020
 */
class InAppMessageComparator : Comparator<InAppMessage> {
    override fun compare(first: InAppMessage, second: InAppMessage): Int {
        if (first.data.priority == second.data.priority) {
            val simpleDateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
            val firstExpireDate = simpleDateFormat.parse(first.data.expireDate)
            val secondExpireDate = simpleDateFormat.parse(second.data.expireDate)
            return if (firstExpireDate == null || secondExpireDate == null) {
                0
            } else if (firstExpireDate.before(secondExpireDate)) {
                -1
            } else if (secondExpireDate.before(firstExpireDate)) {
                1
            } else {
                0
            }
        } else {
            return if (first.data.priority < second.data.priority) -1 else 1
        }
    }
}