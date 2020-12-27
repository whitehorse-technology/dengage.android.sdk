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
    fun saveInboxMessagesToPrefsTest() {
        val id1 = UUID.randomUUID().toString()
        val title1 = "title1"
        val message1 = "message1"
        val mediaUrl1 = "mediaUrl1"
        val targetUrl1 = "targetUrl1"
        val receiveDate1 = "receiveDate1"
        val expireDate1 = "expireDate1"
        val isRead1 = false
        val inboxMessage1 = InboxMessage(id1, title1, message1, mediaUrl1, targetUrl1, receiveDate1, expireDate1, isRead1)

        val id2 = UUID.randomUUID().toString()
        val title2 = "title2"
        val message2 = "message2"
        val mediaUrl2 = "mediaUrl2"
        val targetUrl2 = "targetUrl2"
        val receiveDate2 = "receiveDate2"
        val expireDate2 = "expireDate2"
        val isRead2 = true
        val inboxMessage2 = InboxMessage(id2, title2, message2, mediaUrl2, targetUrl2, receiveDate2, expireDate2, isRead2)

        val inboxMessages = mutableListOf<InboxMessage>()
        inboxMessages.add(inboxMessage1)
        inboxMessages.add(inboxMessage2)

        prefs.inboxMessages = inboxMessages

        Assert.assertNotNull(prefs.inboxMessages)
        Assert.assertEquals(prefs.inboxMessages?.size, 2)
        Assert.assertEquals(prefs.inboxMessages!![0].id, id1)
        Assert.assertEquals(prefs.inboxMessages!![0].title, title1)
        Assert.assertEquals(prefs.inboxMessages!![0].message, message1)
        Assert.assertEquals(prefs.inboxMessages!![0].mediaUrl, mediaUrl1)
        Assert.assertEquals(prefs.inboxMessages!![0].targetUrl, targetUrl1)
        Assert.assertEquals(prefs.inboxMessages!![0].receiveDate, receiveDate1)
        Assert.assertEquals(prefs.inboxMessages!![0].expireDate, expireDate1)
        Assert.assertEquals(prefs.inboxMessages!![0].isRead, isRead1)

        Assert.assertEquals(prefs.inboxMessages!![1].id, id2)
        Assert.assertEquals(prefs.inboxMessages!![1].title, title2)
        Assert.assertEquals(prefs.inboxMessages!![1].message, message2)
        Assert.assertEquals(prefs.inboxMessages!![1].mediaUrl, mediaUrl2)
        Assert.assertEquals(prefs.inboxMessages!![1].targetUrl, targetUrl2)
        Assert.assertEquals(prefs.inboxMessages!![1].receiveDate, receiveDate2)
        Assert.assertEquals(prefs.inboxMessages!![1].expireDate, expireDate2)
        Assert.assertEquals(prefs.inboxMessages!![1].isRead, isRead2)
    }

    @Test
    fun saveInAppMessagesToPrefsTest() {
        val messageId = UUID.randomUUID().toString()
        val messageDetails = "messageDetails"
        val expireDate = "expireDate"
        val dengageSendId = 1
        val dengageCampId = 2
        val content = Content(type = ContentType.FULL_SCREEN.type,
                params = ContentParams(position = ContentPosition.BOTTOM.position,
                        showTitle = true,
                        title = "title", message = "message",
                        showImage = false, imageUrl = null,
                        targetUrl = null,
                        primaryColor = null, secondaryColor = null,
                        backgroundColor = null, shouldAnimate = false))
        val displayCondition = DisplayCondition(screenNameFilters = null, screenDataFilters = null)
        val displayTiming = DisplayTiming(triggerBy = TriggerBy.NAVIGATION.triggerBy, delay = 10, minVisitedScreens = 3)
        val hasShown = false
        val inAppMessage = InAppMessage(messageId, messageDetails, expireDate, dengageSendId, dengageCampId, content, displayCondition, displayTiming, hasShown)

        val inAppMessages = mutableListOf<InAppMessage>()
        inAppMessages.add(inAppMessage)

        prefs.inAppMessages = inAppMessages

        Assert.assertNotNull(prefs.inAppMessages)
        Assert.assertEquals(prefs.inAppMessages?.size, 1)
        Assert.assertEquals(prefs.inAppMessages!![0].messageId, messageId)
        Assert.assertEquals(prefs.inAppMessages!![0].messageDetails, messageDetails)
        Assert.assertEquals(prefs.inAppMessages!![0].expireDate, expireDate)
        Assert.assertEquals(prefs.inAppMessages!![0].dengageSendId, dengageSendId)
        Assert.assertEquals(prefs.inAppMessages!![0].dengageCampId, dengageCampId)
        Assert.assertEquals(prefs.inAppMessages!![0].content.type, content.type)
        Assert.assertEquals(prefs.inAppMessages!![0].content.params.position, content.params.position)
        Assert.assertEquals(prefs.inAppMessages!![0].content.params.showTitle, content.params.showTitle)
        Assert.assertEquals(prefs.inAppMessages!![0].content.params.title, content.params.title)
        Assert.assertEquals(prefs.inAppMessages!![0].content.params.message, content.params.message)
        Assert.assertEquals(prefs.inAppMessages!![0].content.params.showImage, content.params.showImage)
        Assert.assertEquals(prefs.inAppMessages!![0].content.params.imageUrl, content.params.imageUrl)
        Assert.assertEquals(prefs.inAppMessages!![0].content.params.targetUrl, content.params.targetUrl)
        Assert.assertEquals(prefs.inAppMessages!![0].content.params.primaryColor, content.params.primaryColor)
        Assert.assertEquals(prefs.inAppMessages!![0].content.params.secondaryColor, content.params.secondaryColor)
        Assert.assertEquals(prefs.inAppMessages!![0].content.params.backgroundColor, content.params.backgroundColor)
        Assert.assertEquals(prefs.inAppMessages!![0].content.params.shouldAnimate, content.params.shouldAnimate)
        Assert.assertEquals(prefs.inAppMessages!![0].displayCondition.screenNameFilters, displayCondition.screenNameFilters)
        Assert.assertEquals(prefs.inAppMessages!![0].displayCondition.screenDataFilters, displayCondition.screenDataFilters)
        Assert.assertEquals(prefs.inAppMessages!![0].displayTiming.triggerBy, displayTiming.triggerBy)
        Assert.assertEquals(prefs.inAppMessages!![0].displayTiming.delay, displayTiming.delay)
        Assert.assertEquals(prefs.inAppMessages!![0].displayTiming.minVisitedScreens, displayTiming.minVisitedScreens)
        Assert.assertEquals(prefs.inAppMessages!![0].hasShown, hasShown)
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