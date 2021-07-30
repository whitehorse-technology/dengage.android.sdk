package com.dengage.sdk.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AppTracking (
    @SerializedName("alias") var alias: String,
    @SerializedName("packageName") var packageName: String
): Serializable