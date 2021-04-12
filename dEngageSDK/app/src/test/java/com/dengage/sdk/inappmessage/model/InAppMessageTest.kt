package com.dengage.sdk.inappmessage.model

import org.junit.Assert
import org.junit.Test

/**
 * Created by Batuhan Coskun on 27 February 2021
 */
class InAppMessageTest {

    @Test
    fun `InAppMessage constructor test`() {
        val id = Math.random().toString()
        val contentParams = ContentParams(
                position = ContentPosition.BOTTOM.position,
                showTitle = true,
                title = "title",
                message = "message",
                showImage = false,
                imageUrl = null,
                primaryColor = null,
                secondaryColor = null,
                backgroundColor = null,
                shouldAnimate = true
        )
        val content = Content(
                type = ContentType.SMALL.type,
                targetUrl = null,
                params = contentParams
        )

        val displayCondition = DisplayCondition(
                screenNameFilters = null,
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
                expireDate = "expireDate",
                priority = Priority.LOW.priority,
                dengageSendId = Math.random().toInt(),
                dengageCampId = Math.random().toInt(),
                content = content,
                displayCondition = displayCondition,
                displayTiming = displayTiming
        )

        val inAppMessage = InAppMessage(
                id = id,
                data = inAppMessageData
        )

        Assert.assertEquals(id, inAppMessage.id)
        Assert.assertEquals(inAppMessageData, inAppMessage.data)
    }

}