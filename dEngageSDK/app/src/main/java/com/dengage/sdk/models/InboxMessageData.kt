package com.dengage.sdk.models

import java.io.Serializable

/**
 * Created by Batuhan Coskun on 29 January 2021
 */
class InboxMessageData(val title: String?, val message: String?,
                       val mediaUrl: String?, val targetUrl: String?,
                       val receiveDate: String?) : Serializable