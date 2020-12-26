package com.dengage.sdk.inappmessage

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Batuhan Coskun on 26 December 2020
 */
class InAppMessage(@SerializedName("messageId") val messageId: String,
                   @SerializedName("messageDetails") val messageDetails: String?,
                   @SerializedName("expireDate") val expireDate: String,
                   @SerializedName("dengageSendId") val dengageSendId: Int,
                   @SerializedName("dengageCampId") val dengageCampId: Int,
                   @SerializedName("content") val content: Content,
                   @SerializedName("displayCondition") val displayCondition: DisplayCondition,
                   @SerializedName("displayTiming") val displayTiming: DisplayTiming) : Serializable