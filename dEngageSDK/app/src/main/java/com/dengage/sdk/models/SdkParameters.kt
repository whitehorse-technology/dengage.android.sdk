package com.dengage.sdk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Batuhan Coskun on 26 January 2021
 */
class SdkParameters(
        @SerializedName("accountId") val accountId: Int?,
        @SerializedName("accountName") val accountName: String?,
        @SerializedName("eventsEnabled") val eventsEnabled: Boolean,
        @SerializedName("inboxEnabled") val inboxEnabled: Boolean?,
        @SerializedName("inAppEnabled") val inAppEnabled: Boolean?,
        @SerializedName("subscriptionEnabled") val subscriptionEnabled: Boolean?,
        @SerializedName("lastFetchTimeInMillis") val lastFetchTimeInMillis: Long?,
) : Serializable