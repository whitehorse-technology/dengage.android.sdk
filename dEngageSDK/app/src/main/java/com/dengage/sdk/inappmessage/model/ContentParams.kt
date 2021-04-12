package com.dengage.sdk.inappmessage.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Batuhan Coskun on 26 December 2020
 */
class ContentParams(
        @SerializedName("position") val position: String,
        @SerializedName("showTitle") val showTitle: Boolean,
        @SerializedName("title") val title: String?,
        @SerializedName("message") val message: String?,
        @SerializedName("showImage") val showImage: Boolean,
        @SerializedName("imageUrl") val imageUrl: String?,
        @SerializedName("primaryColor") val primaryColor: String?,
        @SerializedName("secondaryColor") val secondaryColor: String?,
        @SerializedName("backgroundColor") val backgroundColor: String?,
        @SerializedName("shouldAnimate") val shouldAnimate: Boolean
) : Serializable