package com.dengage.sdk.inappmessage

import com.dengage.sdk.inappmessage.model.*

/**
 * Created by Batuhan Coskun on 26 February 2021
 */
object InAppMessageMocker {

    fun createInAppMessage(id: String, priority: Priority, expireDate: String,
                           screenName: String? = null, operator: Operator? = null): InAppMessage {
        val contentParams = ContentParams(
                position = ContentPosition.BOTTOM.position,
                showTitle = true,
                title = "title",
                message = "message",
                showImage = false,
                imageUrl = null,
                targetUrl = null,
                primaryColor = null,
                secondaryColor = null,
                backgroundColor = null,
                shouldAnimate = true
        )
        val content = Content(
                type = ContentType.SMALL.type,
                params = contentParams
        )

        val displayCondition = DisplayCondition(
                screenNameFilters = if (screenName != null && operator != null) listOf(ScreenNameFilter(
                        value = screenName,
                        operator = operator.operator
                ))
                else null,
                screenDataFilters = null
        )
        val displayTiming = DisplayTiming(
                triggerBy = TriggerBy.NAVIGATION.triggerBy,
                delay = 10,
                showEveryXMinutes = 5
        )
        val inAppMessageData = InAppMessageData(
                messageId = Math.random().toString(),
                messageDetails = "messageDetails",
                expireDate = expireDate,
                priority = priority.priority,
                dengageSendId = Math.random().toInt(),
                dengageCampId = Math.random().toInt(),
                content = content,
                displayCondition = displayCondition,
                displayTiming = displayTiming
        )

        return InAppMessage(
                id = id,
                data = inAppMessageData
        )
    }

}