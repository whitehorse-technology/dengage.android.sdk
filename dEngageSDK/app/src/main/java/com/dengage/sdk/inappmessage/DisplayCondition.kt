package com.dengage.sdk.inappmessage

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by Batuhan Coskun on 26 December 2020
 */
class DisplayCondition(@SerializedName("screenNameFilters") val screenNameFilters: List<ScreenNameFilter>,
                       @SerializedName("screenDataFilters") val screenDataFilters: List<ScreenDataFilter>) : Serializable