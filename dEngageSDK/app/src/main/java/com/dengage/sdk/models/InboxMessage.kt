package com.dengage.sdk.models

import java.io.Serializable

/**
 * Created by Batuhan Coskun on 30 November 2020
 */
class InboxMessage(val id: String, var isClicked: Boolean,
                   val data: InboxMessageData) : Serializable