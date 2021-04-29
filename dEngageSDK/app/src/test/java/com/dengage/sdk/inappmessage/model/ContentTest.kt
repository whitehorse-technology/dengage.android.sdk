package com.dengage.sdk.inappmessage.model

import org.junit.Assert
import org.junit.Test

/**
 * Created by Batuhan Coskun on 27 February 2021
 */
class ContentTest {

    @Test
    fun `Content constructor test`() {
        val contentType = ContentType.SMALL.type
        val targetUrl = "targetUrl"
        val contentParams = ContentParams(
            position = ContentPosition.BOTTOM.position,
            showTitle = true,
            title = "title",
            message = "message",
            showImage = false,
            imageUrl = null,
            primaryColor = null,
            secondaryColor = null,
            backgroundColor = null,
            shouldAnimate = true,
            html = null,
            maxWidth = null,
            radius = null
        )
        val content = Content(
            type = contentType,
            targetUrl = targetUrl,
            params = contentParams
        )
        Assert.assertEquals(contentType, content.type)
        Assert.assertEquals(targetUrl, content.targetUrl)
        Assert.assertEquals(contentParams, content.params)
    }

}