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
        val contentParams = ContentParams(
                position = ContentPosition.BOTTOM.position,
                showTitle = true,
                title = "title",
                message = "message",
                showImage = false,
                imageUrl = null,
                targetUrl = null,
                primaryColor = null,
                secondaryColor = null,
                backgroundColor = null,
                shouldAnimate = true
        )
        val content = Content(
                type = contentType,
                params = contentParams
        )
        Assert.assertEquals(contentType, content.type)
        Assert.assertEquals(contentParams, content.params)
    }

}