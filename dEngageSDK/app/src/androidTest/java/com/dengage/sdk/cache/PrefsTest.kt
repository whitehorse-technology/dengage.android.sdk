package com.dengage.sdk.cache

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.dengage.sdk.inappmessage.model.*
import com.dengage.sdk.models.InboxMessage
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.util.*

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
    fun saveSessionNavigationCountToPrefsTest() {
        val sessionNavigationCount = 2
        prefs.sessionNavigationCount = sessionNavigationCount

        Assert.assertEquals(prefs.sessionNavigationCount, sessionNavigationCount)
    }

    @Test
    fun clearPrefsTest() {
        prefs.sessionNavigationCount = 1
        prefs.clear()

        Assert.assertEquals(prefs.sessionNavigationCount, 0)
    }

}