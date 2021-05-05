package com.dengage.sdk.service

import com.dengage.sdk.models.InboxMessageData
import com.google.gson.*
import java.lang.reflect.Type


/**
 * Created by Batuhan Coskun on 02 February 2021
 */
class InboxMessageDataJsonAdapter : JsonDeserializer<InboxMessageData>, JsonSerializer<InboxMessageData?> {

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): InboxMessageData {
        return if (json.isJsonObject) {
            Gson().fromJson(json.asJsonObject, InboxMessageData::class.java)
        } else {
            val str: String = json.asJsonPrimitive.asString
            Gson().fromJson(str, InboxMessageData::class.java)
        }
    }

    override fun serialize(src: InboxMessageData?, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return context.serialize(src)
    }

}