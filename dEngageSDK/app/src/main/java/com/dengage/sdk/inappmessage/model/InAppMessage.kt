package com.dengage.sdk.inappmessage.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Batuhan Coskun on 26 December 2020
 */
class InAppMessage(
    @SerializedName("smsg_id") val id: String,
    @SerializedName("message_json") val data: InAppMessageData
) : Serializable