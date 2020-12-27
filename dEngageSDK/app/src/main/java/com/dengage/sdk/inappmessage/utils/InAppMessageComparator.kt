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
        val firstHasShown = first.hasShown
        val secondHasShown = second.hasShown

        if (firstHasShown == secondHasShown) {
            val simpleDateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
            val firstExpireDate = simpleDateFormat.parse(first.expireDate)
            val secondExpireDate = simpleDateFormat.parse(second.expireDate)
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
            return if (firstHasShown) -1 else 1
        }
    }
}