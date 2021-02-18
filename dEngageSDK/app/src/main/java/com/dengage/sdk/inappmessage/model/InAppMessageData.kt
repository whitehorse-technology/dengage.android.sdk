package com.dengage.sdk.inappmessage.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Batuhan Coskun on 19 February 2021
 */
class InAppMessageData(
        @SerializedName("messageId") val messageId: String,
        @SerializedName("messageDetails") val messageDetails: String?,
        @SerializedName("expireDate") val expireDate: String,
        @SerializedName("dengageSendId") val dengageSendId: Int,
        @SerializedName("dengageCampId") val dengageCampId: Int,
        @SerializedName("content") val content: Content,
        @SerializedName("displayCondition") val displayCondition: DisplayCondition,
        @SerializedName("displayTiming") val displayTiming: DisplayTiming
) : Serializable