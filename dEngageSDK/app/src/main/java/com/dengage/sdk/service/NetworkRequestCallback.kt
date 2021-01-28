package com.dengage.sdk.service

import com.dengage.sdk.models.DengageError

/**
 * Created by Batuhan Coskun on 26 January 2021
 */
interface NetworkRequestCallback {

    fun responseFetched(response: String?)
    fun requestError(error: DengageError)

}