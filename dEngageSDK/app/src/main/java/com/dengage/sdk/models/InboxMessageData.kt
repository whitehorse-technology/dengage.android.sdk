package com.dengage.sdk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Batuhan Coskun on 29 January 2021
 */
class InboxMessageData(
    @SerializedName("title") val title: String?,
    @SerializedName("message")  val message: String?,
    @SerializedName("androidMediaUrl") val mediaUrl: String?,
    @SerializedName("androidTargetUrl") val targetUrl: String?,
    @SerializedName("receiveDate")  val receiveDate: String?
) : Serializable