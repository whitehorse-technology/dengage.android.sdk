package com.dengage.sdk.inappmessage.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Batuhan Coskun on 26 December 2020
 */
class ContentParams(
    @SerializedName("position") val position: String,
    @SerializedName("shouldAnimate") val shouldAnimate: Boolean,
    @SerializedName("html") val html: String?,
    @SerializedName("maxWidth") val maxWidth: Int?,
    @SerializedName("radius") val radius: Int?,
    @SerializedName("marginTop") val marginTop: Int?,
    @SerializedName("marginBottom") val marginBottom: Int?,
    @SerializedName("marginLeft") val marginLeft: Int?,
    @SerializedName("marginRight") val marginRight: Int?,
    @SerializedName("dismissOnTouchOutside") val dismissOnTouchOutside: Boolean?
) : Serializable