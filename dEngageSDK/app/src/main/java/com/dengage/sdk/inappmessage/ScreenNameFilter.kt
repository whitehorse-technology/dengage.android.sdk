package com.dengage.sdk.inappmessage

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Batuhan Coskun on 26 December 2020
 */
class ScreenNameFilter(@SerializedName("operator") val operator: String,
                       @SerializedName("value") val value: String) : Serializable