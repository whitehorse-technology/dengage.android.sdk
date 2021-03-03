package com.dengage.sdk.inappmessage.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Batuhan Coskun on 26 December 2020
 */
class DisplayTiming(
        @SerializedName("triggerBy") var triggerBy: String,
        @SerializedName("delay") val delay: Int?,
        @SerializedName("showEveryXMinutes") val showEveryXMinutes: Int?
) : Serializable