package com.dengage.sdk.service

import android.os.AsyncTask
import com.dengage.sdk.Request
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by Batuhan Coskun on 26 January 2021
 */
 class NetworkRequest<T>(private val url: String, private val userAgent: String,
                         private val networkRequestType: NetworkRequestType,
                         private val networkRequestCallback: NetworkRequestCallback<T>)
    : AsyncTask<Void, String?, String>() {

    override fun doInBackground(vararg params: Void?): String? {
        return when (networkRequestType) {
            NetworkRequestType.SDK_PARAMS -> {
                Request().getSdkParameters(url, userAgent)
            }
            else -> {
                null
            }
        }
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        val response: T? = if (result.isNullOrEmpty()) null
        else Gson().fromJson(result, object : TypeToken<T>() {}.type)
        networkRequestCallback.responseFetched(response)
    }

    fun executeTask() {
        executeOnExecutor(THREAD_POOL_EXECUTOR)
    }

}