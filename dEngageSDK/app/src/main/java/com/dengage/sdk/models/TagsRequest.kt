package com.dengage.sdk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

/**
 * Created by Batuhan Coskun on 24 March 2021
 */
data class TagsRequest(
        @SerializedName("accountName") val accountName: String,
        @SerializedName("key") val key: String,
        @SerializedName("tags") val tags: List<HashMap<String, String>>
) : Serializable