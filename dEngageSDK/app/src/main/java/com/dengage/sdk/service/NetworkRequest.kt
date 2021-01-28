package com.dengage.sdk.service

import android.os.AsyncTask
import com.dengage.sdk.Request
import com.dengage.sdk.models.DengageError

/**
 * Created by Batuhan Coskun on 26 January 2021
 */
class NetworkRequest(private val url: String, private val userAgent: String,
                     private val networkRequestCallback: NetworkRequestCallback?)
    : AsyncTask<Void, Any?, Any>() {

    override fun doInBackground(vararg params: Void?): Any? {
        return try {
            Request().sendRequest(url, userAgent)
        } catch (e: Exception) {
            e
        }
    }

    override fun onPostExecute(result: Any?) {
        super.onPostExecute(result)
        if (result == null || result is String?) {
            networkRequestCallback?.responseFetched(result as String?)
        } else if (result is Exception) {
            networkRequestCallback?.requestError(DengageError("Api Error: ${result.message}"))
        }
    }

    fun executeTask() {
        executeOnExecutor(THREAD_POOL_EXECUTOR)
    }

}