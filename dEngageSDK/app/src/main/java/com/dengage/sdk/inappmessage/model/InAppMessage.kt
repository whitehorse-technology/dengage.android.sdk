package com.dengage.sdk.inappmessage.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Batuhan Coskun on 26 December 2020
 */
class InAppMessage(
        @SerializedName("id") val id: String,
        @SerializedName("data") val data: InAppMessageData,
        @SerializedName("hasShown") val hasShown: Boolean = false
) : Serializable