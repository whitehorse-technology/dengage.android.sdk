package com.dengage.sdk.models

import org.junit.Assert
import org.junit.Test
import java.util.*

/**
 * Created by Batuhan Coskun on 27 December 2020
 */
class InboxMessageTest {

    @Test
    fun `InboxMessage constructor test`() {
        val id = UUID.randomUUID().toString()
        val title = "title"
        val message = "message"
        val mediaUrl = "mediaUrl"
        val androidMediaUrl = "androidMediaUrl"
        val targetUrl = "targetUrl"
        val androidTargetUrl = "androidTargetUrl"
        val receiveDate = "receiveDate"
        val isClicked = false
        val inboxMessageData = InboxMessageData(
            title = title,
            message = message,
            mediaUrl = mediaUrl,
            androidMediaUrl = androidMediaUrl,
            targetUrl = targetUrl,
            androidTargetUrl = androidTargetUrl,
            receiveDate = receiveDate
        )
        val inboxMessage = InboxMessage(
            id = id,
            isClicked = isClicked,
            data = inboxMessageData
        )

        Assert.assertEquals(id, inboxMessage.id)
        Assert.assertEquals(title, inboxMessage.data.title)
        Assert.assertEquals(message, inboxMessage.data.message)
        Assert.assertEquals(mediaUrl, inboxMessage.data.mediaUrl)
        Assert.assertEquals(androidMediaUrl, inboxMessage.data.androidMediaUrl)
        Assert.assertEquals(targetUrl, inboxMessage.data.targetUrl)
        Assert.assertEquals(androidTargetUrl, inboxMessage.data.androidTargetUrl)
        Assert.assertEquals(receiveDate, inboxMessage.data.receiveDate)
        Assert.assertEquals(isClicked, inboxMessage.isClicked)
    }

}