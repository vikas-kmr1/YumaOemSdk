package com.yumacustomer.core_analytics.impl.utils

import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

// Extension function to convert Map<String, Any> to JsonObject
fun Map<String, Any>.toJsonObject(): JsonObject {
    return buildJsonObject {
        this@toJsonObject.forEach { (key, value) ->
            when (value) {
                is String -> put(key, JsonPrimitive(value))
                is Number -> put(key, JsonPrimitive(value))
                is Boolean -> put(key, JsonPrimitive(value))
                else -> put(key, JsonPrimitive(value.toString())) // Convert unsupported types to string
            }
        }
    }
}