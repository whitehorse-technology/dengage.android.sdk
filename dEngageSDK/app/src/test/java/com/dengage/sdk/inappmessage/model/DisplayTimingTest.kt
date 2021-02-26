package com.dengage.sdk.inappmessage.model

import org.junit.Assert
import org.junit.Test

/**
 * Created by Batuhan Coskun on 27 February 2021
 */
class DisplayTimingTest {

    @Test
    fun `DisplayTiming constructor test`() {
        val triggerBy = TriggerBy.NAVIGATION.triggerBy
        val delay = 10
        val minVisitedScreens = 20

        val displayTiming = DisplayTiming(
                triggerBy = triggerBy,
                delay = delay,
                minVisitedScreens = minVisitedScreens
        )

        Assert.assertEquals(triggerBy, displayTiming.triggerBy)
        Assert.assertEquals(delay, displayTiming.delay)
        Assert.assertEquals(minVisitedScreens, displayTiming.minVisitedScreens)
    }

}