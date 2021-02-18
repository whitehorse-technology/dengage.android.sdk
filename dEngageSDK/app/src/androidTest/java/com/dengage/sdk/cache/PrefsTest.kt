package com.dengage.sdk.cache

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.dengage.sdk.models.SdkParameters
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Created by Batuhan Coskun on 27 December 2020
 */
class PrefsTest {

    private lateinit var context: Context
    private lateinit var prefs: Prefs

    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().context
        prefs = Prefs(context)
    }

    @After
    fun tearDown() {
        prefs.clear()
    }

    @Test
    fun saveSdkParametersToPrefsTest() {
        val accountId = 1
        val accountName = "accountName"
        val eventsEnabled = true
        val inboxEnabled = true
        val inAppEnabled = true
        val subscriptionEnabled = true
        val lastFetchTimeInMillis = 10L

        val sdkParameters = SdkParameters(
                accountId = accountId,
                accountName = accountName,
                eventsEnabled = eventsEnabled,
                inboxEnabled = inboxEnabled,
                inAppEnabled = inAppEnabled,
                subscriptionEnabled = subscriptionEnabled,
                lastFetchTimeInMillis = lastFetchTimeInMillis
        )
        prefs.sdkParameters = sdkParameters

        Assert.assertEquals(prefs.sdkParameters?.accountId, accountId)
        Assert.assertEquals(prefs.sdkParameters?.accountName, accountName)
        Assert.assertEquals(prefs.sdkParameters?.eventsEnabled, eventsEnabled)
        Assert.assertEquals(prefs.sdkParameters?.inboxEnabled, inboxEnabled)
        Assert.assertEquals(prefs.sdkParameters?.inAppEnabled, inAppEnabled)
        Assert.assertEquals(prefs.sdkParameters?.subscriptionEnabled, subscriptionEnabled)
        Assert.assertEquals(prefs.sdkParameters?.lastFetchTimeInMillis, lastFetchTimeInMillis)
    }

    @Test
    fun saveSessionNavigationCountToPrefsTest() {
        val sessionNavigationCount = 2
        prefs.sessionNavigationCount = sessionNavigationCount
        Assert.assertEquals(prefs.sessionNavigationCount, sessionNavigationCount)
    }

    @Test
    fun saveInAppMessageFetchTimeToPrefsTest() {
        val time = 20L
        prefs.inAppMessageFetchTime = time
        Assert.assertEquals(prefs.inAppMessageFetchTime, time)
    }

    @Test
    fun clearPrefsTest() {
        prefs.sessionNavigationCount = 1
        prefs.clear()
        Assert.assertEquals(prefs.sessionNavigationCount, 0)
    }

}