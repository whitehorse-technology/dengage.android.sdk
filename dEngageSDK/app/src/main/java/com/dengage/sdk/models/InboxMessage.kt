package com.dengage.sdk.models

import com.dengage.sdk.Constants
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Batuhan Coskun on 30 November 2020
 */
class InboxMessage(val id: String?, val title: String?, val message: String?,
                   val mediaUrl: String?, val targetUrl: String?, val receiveDate: String?,
                   val expireDate: String?, var isRead: Boolean) : Serializable {

    companion object {
        fun createWith(message: Message): InboxMessage {
            val expireDateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
            return InboxMessage(
                    id = "{${message.sendId}}-{${message.messageId}}",
                    title = message.title, message = message.message,
                    mediaUrl = message.mediaUrl, targetUrl = message.targetUrl,
                    receiveDate = expireDateFormat.format(Date()),
                    expireDate = message.expireDate, isRead = false
            )
        }
    }
}