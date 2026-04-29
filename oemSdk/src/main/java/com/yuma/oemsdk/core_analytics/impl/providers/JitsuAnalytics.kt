package com.yuma.oemsdk.core_analytics.impl.providers

import com.segment.analytics.kotlin.core.Analytics
import com.yumacustomer.core_analytics.impl.utils.toJsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SegmentAnalytics(
    private var analytics: Analytics?,
) {

    suspend fun postEvent(eventName: String) {
        withContext(Dispatchers.IO) {
            analytics?.track(eventName)
        }
    }

    suspend fun postEvent(event: String, value: String) {
        withContext(Dispatchers.IO) {
            analytics?.track(event, buildJsonObject { put("value", value) })
        }
    }

    suspend fun postEvent(event: String, key: String, value: String) {
        withContext(Dispatchers.IO) {
            analytics?.track(event, buildJsonObject { put(key, value) })
        }
    }

    suspend fun postEvent(event: String, values: Map<String, Any>) {
        withContext(Dispatchers.IO) {
            analytics?.track(event, values.toJsonObject())
        }
    }

    suspend fun postEvent(event: String, properties: JsonObject) {
        withContext(Dispatchers.IO) {
            analytics?.track(event, properties)
        }
    }

    suspend fun identify(userId: String, traits: Map<String, Any>) {
        withContext(Dispatchers.IO) {
            analytics?.identify(userId, traits.toJsonObject())
        }
    }

    suspend fun setUserProperties(properties: Map<String, Any>) {
        withContext(Dispatchers.IO) {
            analytics?.identify(enrichment = null)
        }
    }

    suspend fun flush() {
        withContext(Dispatchers.IO) {
            analytics?.flush()
        }
    }

    suspend fun tearDown() {
        withContext(Dispatchers.IO) {
            analytics?.flush()
            analytics = null
        }
    }

    suspend fun resetUser() {
        withContext(Dispatchers.IO) {
            analytics?.reset()
        }
    }
}
