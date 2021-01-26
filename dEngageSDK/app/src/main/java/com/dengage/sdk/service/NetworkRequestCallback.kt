package com.dengage.sdk.service

/**
 * Created by Batuhan Coskun on 26 January 2021
 */
interface NetworkRequestCallback<T> {

    fun responseFetched(response: T?)

}