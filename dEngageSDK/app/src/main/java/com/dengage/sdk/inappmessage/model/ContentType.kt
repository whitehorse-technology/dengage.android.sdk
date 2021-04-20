package com.dengage.sdk.inappmessage.model

/**
 * Created by Batuhan Coskun on 26 December 2020
 */
enum class ContentType(val type: String) {
    SMALL("SMALL"),
    SMALL_BUTTON("SMALL_BUTTON"),
    POP_OUT_MODAL("POPOUT_MODAL"),
    FULL_SCREEN("FULL_SCREEN")
}