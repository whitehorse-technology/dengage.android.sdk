package com.dengage.sdk.inappmessage.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Batuhan Coskun on 26 December 2020
 */
class ScreenDataFilter(
        @SerializedName("dataName") val dataName: String,
        @SerializedName("type") val type: String,
        @SerializedName("operator") val operator: String,
        val value: List<String>
) : Serializable