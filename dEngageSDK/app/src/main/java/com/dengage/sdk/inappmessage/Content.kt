package com.dengage.sdk.inappmessage

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Batuhan Coskun on 26 December 2020
 */
class Content(@SerializedName("type") val type: String,
              @SerializedName("props") val params: ContentParams) : Serializable
