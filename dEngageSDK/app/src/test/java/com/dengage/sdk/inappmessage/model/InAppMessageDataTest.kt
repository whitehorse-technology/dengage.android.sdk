package com.dengage.sdk.inappmessage.model

import org.junit.Assert
import org.junit.Test

/**
 * Created by Batuhan Coskun on 27 February 2021
 */
class InAppMessageDataTest {

    @Test
    fun `InAppMessageData constructor test`() {
        val messageId = Math.random().toString()
        val messageDetails = "messageDetails"
        val expireDate = "expireDate"
        val priority = Priority.HIGH.priority
        val dengageSendId = Math.random().toInt()
        val dengageCampId = Math.random().toInt()

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
                screenNameFilters = null,
                screenDataFilters = null
        )
        val displayTiming = DisplayTiming(
                triggerBy = TriggerBy.NAVIGATION.triggerBy,
                delay = 10,
                minVisitedScreens = 5
        )
        val inAppMessageData = InAppMessageData(
                messageId = messageId,
                messageDetails = messageDetails,
                expireDate = expireDate,
                priority = priority,
                dengageSendId = dengageSendId,
                dengageCampId = dengageCampId,
                content = content,
                displayCondition = displayCondition,
                displayTiming = displayTiming
        )

        Assert.assertEquals(messageId, inAppMessageData.messageId)
        Assert.assertEquals(messageDetails, inAppMessageData.messageDetails)
        Assert.assertEquals(expireDate, inAppMessageData.expireDate)
        Assert.assertEquals(priority, inAppMessageData.priority)
        Assert.assertEquals(dengageSendId, inAppMessageData.dengageSendId)
        Assert.assertEquals(dengageCampId, inAppMessageData.dengageCampId)
        Assert.assertEquals(content, inAppMessageData.content)
        Assert.assertEquals(displayCondition, inAppMessageData.displayCondition)
        Assert.assertEquals(displayTiming, inAppMessageData.displayTiming)
    }

}