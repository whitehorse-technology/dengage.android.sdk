package com.dengage.sdk.service

import com.dengage.sdk.models.InboxMessageData
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * Created by Batuhan Coskun on 02 February 2021
 */
class InboxMessageDataJsonAdapter : JsonDeserializer<InboxMessageData>, JsonSerializer<InboxMessageData?> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): InboxMessageData {
        val inboxMessageData = if (json.isJsonObject) {
            Gson().fromJson(json.asJsonObject, InboxMessageData::class.java)
        } else {
            val str: String = json.asJsonPrimitive.asString
            Gson().fromJson(str, InboxMessageData::class.java)
        }
        inboxMessageData.mediaUrl = inboxMessageData.androidMediaUrl ?: inboxMessageData.mediaUrl
        inboxMessageData.targetUrl = inboxMessageData.androidTargetUrl ?: inboxMessageData.targetUrl
        return inboxMessageData
    }

    override fun serialize(src: InboxMessageData?, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(src)
    }

}