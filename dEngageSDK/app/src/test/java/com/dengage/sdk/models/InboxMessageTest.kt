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
        val targetUrl = "targetUrl"
        val receiveDate = "receiveDate"
        val isClicked = false
        val inboxMessageData = InboxMessageData(
                title = title,
                message = message,
                mediaUrl = mediaUrl,
                targetUrl = targetUrl,
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
        Assert.assertEquals(targetUrl, inboxMessage.data.targetUrl)
        Assert.assertEquals(receiveDate, inboxMessage.data.receiveDate)
        Assert.assertEquals(isClicked, inboxMessage.isClicked)
    }

}