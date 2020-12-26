package com.dengage.sdk.inappmessage

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Batuhan Coskun on 26 December 2020
 */
class DisplayTiming(
        @SerializedName("triggerBy") val triggerBy: String,
        @SerializedName("delay") val delay: Int?,
        @SerializedName("minVisitedScreens") val minVisitedScreens: Int?
) : Serializable