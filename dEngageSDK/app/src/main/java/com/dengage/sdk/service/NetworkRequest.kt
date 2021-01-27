package com.dengage.sdk.service

import android.os.AsyncTask
import com.dengage.sdk.Request

/**
 * Created by Batuhan Coskun on 26 January 2021
 */
class NetworkRequest(private val url: String, private val userAgent: String,
                     private val networkRequestType: NetworkRequestType,
                     private val networkRequestCallback: NetworkRequestCallback)
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
        networkRequestCallback.responseFetched(result)
    }

    fun executeTask() {
        executeOnExecutor(THREAD_POOL_EXECUTOR)
    }

}