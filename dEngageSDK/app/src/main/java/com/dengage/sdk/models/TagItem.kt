package com.dengage.sdk.models

import com.dengage.sdk.Constants
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Batuhan Coskun on 28 March 2021
 */
class TagItem(
        @SerializedName("tag") val tag: String,
        @SerializedName("value") val value: String
) : Serializable {

    @SerializedName("changeTime")
    var changeTime: String? = null

    @SerializedName("changeValue")
    var changeValue: String? = null

    @SerializedName("removeTime")
    var removeTime: String? = null

    constructor(
            tag: String,
            value: String,
            changeTime: Date?,
            changeValue: String?,
            removeTime: Date?
    ) : this(tag, value) {
        val simpleDateFormat = SimpleDateFormat(Constants.DATE_FORMAT, Locale.getDefault())
        this.changeValue = changeValue
        changeTime?.let {
            this.changeTime = simpleDateFormat.format(it)
        }
        removeTime?.let {
            this.removeTime = simpleDateFormat.format(it)
        }
    }
}