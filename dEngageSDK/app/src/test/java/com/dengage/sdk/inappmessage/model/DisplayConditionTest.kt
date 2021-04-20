package com.dengage.sdk.inappmessage.model

import org.junit.Assert
import org.junit.Test

/**
 * Created by Batuhan Coskun on 27 February 2021
 */
class DisplayConditionTest {

    @Test
    fun `DisplayCondition constructor test`() {
        val screenName = "screenName"
        val operator = Operator.STARTS_WITH.operator
        val displayCondition = DisplayCondition(
                screenNameFilters = listOf(ScreenNameFilter(
                        value = listOf(screenName),
                        operator = operator
                )),
                screenDataFilters = null
        )
        Assert.assertEquals(displayCondition.screenNameFilters?.get(0)?.value?.get(0), screenName)
        Assert.assertEquals(displayCondition.screenNameFilters?.get(0)?.operator, operator)
    }

}