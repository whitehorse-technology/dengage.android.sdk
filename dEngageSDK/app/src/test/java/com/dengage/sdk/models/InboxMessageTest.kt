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
        val expireDate = "expireDate"
        val isRead = false
        val inboxMessage = InboxMessage(
                id = id,
                title = title,
                message = message,
                mediaUrl = mediaUrl,
                targetUrl = targetUrl,
                receiveDate = receiveDate,
                expireDate = expireDate,
                isRead = isRead
        )

        Assert.assertEquals(id, inboxMessage.id)
        Assert.assertEquals(title, inboxMessage.title)
        Assert.assertEquals(message, inboxMessage.message)
        Assert.assertEquals(mediaUrl, inboxMessage.mediaUrl)
        Assert.assertEquals(targetUrl, inboxMessage.targetUrl)
        Assert.assertEquals(receiveDate, inboxMessage.receiveDate)
        Assert.assertEquals(expireDate, inboxMessage.expireDate)
        Assert.assertEquals(isRead, inboxMessage.isRead)
    }

    @Test
    fun `Create InboxMessage from Message test`() {
        val sendId = "1"
        val messageId = "2"
        val title = "title"
        val message = "message"
        val mediaUrl = "mediaUrl"
        val targetUrl = "targetUrl"
        val expireDate = "expireDate"

        val messageMap = mutableMapOf<String, String>()
        messageMap["dengageSendId"] = sendId
        messageMap["messageId"] = messageId
        messageMap["title"] = title
        messageMap["message"] = message
        messageMap["mediaUrl"] = mediaUrl
        messageMap["targetUrl"] = targetUrl
        messageMap["expireDate"] = expireDate
        val messagePush = Message(messageMap)
        val inboxMessage = InboxMessage.createWith(messagePush)

        Assert.assertEquals("{$sendId}-{$messageId}", inboxMessage.id)
        Assert.assertEquals(title, inboxMessage.title)
        Assert.assertEquals(message, inboxMessage.message)
        Assert.assertEquals(mediaUrl, inboxMessage.mediaUrl)
        Assert.assertEquals(targetUrl, inboxMessage.targetUrl)
        Assert.assertEquals(expireDate, inboxMessage.expireDate)
        Assert.assertEquals(false, inboxMessage.isRead)
    }

}