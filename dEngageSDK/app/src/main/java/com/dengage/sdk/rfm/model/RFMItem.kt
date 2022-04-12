package com.dengage.sdk.rfm.model

open class RFMItem(
    var id: String,
    var categoryId: String,
    var personalized: Boolean,
    var gender: RFMGender,
    var sequence: Int
)
