package com.dengage.sdk.inappmessage.model

import org.junit.Assert
import org.junit.Test

/**
 * Created by Batuhan Coskun on 27 February 2021
 */
class ContentParamsTest {

    @Test
    fun `ContentParams constructor test`() {
        val position = ContentPosition.BOTTOM.position
        val showTitle = false
        val title = "title"
        val message = "message"
        val showImage = false
        val imageUrl = "imageUrl"
        val targetUrl = "targetUrl"
        val primaryColor = "primaryColor"
        val secondaryColor = "secondaryColor"
        val backgroundColor = "backgroundColor"
        val shouldAnimate = true

        val contentParams = ContentParams(
                position = position,
                showTitle = showTitle,
                title = title,
                message = message,
                showImage = showImage,
                imageUrl = imageUrl,
                targetUrl = targetUrl,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor,
                backgroundColor = backgroundColor,
                shouldAnimate = shouldAnimate
        )

        Assert.assertEquals(position, contentParams.position)
        Assert.assertEquals(showTitle, contentParams.showTitle)
        Assert.assertEquals(title, contentParams.title)
        Assert.assertEquals(message, contentParams.message)
        Assert.assertEquals(showImage, contentParams.showImage)
        Assert.assertEquals(imageUrl, contentParams.imageUrl)
        Assert.assertEquals(targetUrl, contentParams.targetUrl)
        Assert.assertEquals(primaryColor, contentParams.primaryColor)
        Assert.assertEquals(secondaryColor, contentParams.secondaryColor)
        Assert.assertEquals(backgroundColor, contentParams.backgroundColor)
        Assert.assertEquals(shouldAnimate, contentParams.shouldAnimate)
    }

}