package com.dengage.sdk.models

import com.dengage.sdk.service.InboxMessageDataJsonAdapter
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Batuhan Coskun on 30 November 2020
 */
class InboxMessage(@SerializedName("smsg_id") val id: String,
                   @SerializedName("is_clicked") var isClicked: Boolean,
                   @SerializedName("message_json") @JsonAdapter(InboxMessageDataJsonAdapter::class)
                   val data: InboxMessageData) : Serializable