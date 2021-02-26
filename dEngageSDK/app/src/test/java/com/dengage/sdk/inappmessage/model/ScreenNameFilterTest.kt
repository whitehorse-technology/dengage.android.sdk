package com.dengage.sdk.inappmessage.model

import org.junit.Assert
import org.junit.Test

/**
 * Created by Batuhan Coskun on 27 February 2021
 */
class ScreenNameFilterTest {

    @Test
    fun `ScreenNameFilter constructor test`() {
        val screenName = "screenName"
        val operator = Operator.STARTS_WITH.operator
        val screenNameFilter = ScreenNameFilter(
                value = screenName,
                operator = operator
        )
        
        Assert.assertEquals(screenNameFilter.value, screenName)
        Assert.assertEquals(screenNameFilter.operator, operator)
    }

}