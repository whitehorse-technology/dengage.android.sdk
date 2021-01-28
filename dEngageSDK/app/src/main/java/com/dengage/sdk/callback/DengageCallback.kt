package com.dengage.sdk.callback

import com.dengage.sdk.models.DengageError

/**
 * Created by Batuhan Coskun on 29 January 2021
 */
interface DengageCallback<T> {

    fun onResult(result: T)
    fun onError(error: DengageError)

}