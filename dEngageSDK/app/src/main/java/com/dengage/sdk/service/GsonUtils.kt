package com.dengage.sdk.service

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * Created by Batuhan Coskun on 27 January 2021
 */
object GsonUtils {

    val gson: Gson by lazy {
        GsonBuilder()
                .create()
    }

    fun toJson(source: Any?): String {
        return gson.toJson(source)
    }

    inline fun <reified T : Any> fromJson(json: String?): T? {
        return try {
            val type = object : TypeToken<T>() {}.type
            return gson.fromJson<T>(json, type)
        } catch (ex: Exception) {
            Log.e("GsonHolder", "Gson parse error: $ex")
            null
        }
    }

}