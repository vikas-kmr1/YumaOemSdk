package com.yumacustomer.core_analytics.impl

import com.yuma.oemsdk.core_analytics.impl.providers.SegmentAnalytics
import com.yumacustomer.core_analytics.api.AnalyticsApi
import com.yumacustomer.core_logger.api.LoggerApi
import kotlinx.serialization.json.JsonObject

class AnalyticsManager(
    private val segmentAnalytics: SegmentAnalytics,
    private var enableAnalytics: Boolean = true,
    private val enableLogging: Boolean = true,
    private val loggerApi: LoggerApi
) : AnalyticsApi {

    companion object {
        private const val LOG_TAG = "AnalyticsManager"
    }

    override suspend fun postEvent(eventName: String) {
        execute("postEvent($eventName)") {
            segmentAnalytics.postEvent(eventName)
        }
    }

    override suspend fun postEvent(event: String, value: String) {
        execute("postEvent($event, $value)") {
            segmentAnalytics.postEvent(event, value)
        }
    }

    override suspend fun postEvent(event: String, key: String, value: String) {
        execute("postEvent($event, $key=$value)") {
            segmentAnalytics.postEvent(event, key, value)
        }
    }

    override suspend fun postEvent(event: String, values: Map<String, Any>) {
        execute("postEvent($event, $values)") {
            segmentAnalytics.postEvent(event, values)
        }
    }

    override suspend fun postEvent(event: String, properties: JsonObject) {
        execute("postEvent($event, $properties)") {
            segmentAnalytics.postEvent(event, properties)
        }
    }

    override suspend fun identify(userId: String, traits: Map<String, Any>) {
        execute("identify($userId, traits=${traits.keys})") {
            segmentAnalytics.identify(userId, traits)
        }
    }

    override suspend fun setUserProperties(properties: Map<String, Any>) {
        execute("setUserProperties(${properties.keys})") {
            segmentAnalytics.setUserProperties(properties)
        }
    }

    override suspend fun flush() {
        execute("flush()") {
            segmentAnalytics.flush()
        }
    }

    override suspend fun tearDown() {
        execute("tearDown()") {
            segmentAnalytics.tearDown()
        }
    }

    override fun setAnalyticsEnabled(enabled: Boolean) {
        enableAnalytics = enabled
        if (enableLogging) {
            loggerApi.logDWithTag(LOG_TAG, "Analytics enabled: $enableAnalytics")
        }
    }

    override suspend fun resetUser() {
        execute("resetUser()") {
            segmentAnalytics.resetUser()
        }
    }

    /**
     * Centralized safe execution block with logging and enable check
     */
    private suspend inline fun execute(action: String, crossinline block: suspend () -> Unit) {
        if (!enableAnalytics) {
            if (enableLogging) loggerApi.logDWithTag(LOG_TAG, "Skipped: $action (analytics disabled)")
            return
        }

        try {
            if (enableLogging) loggerApi.logDWithTag(LOG_TAG, "Executing: $action")
            block()
        } catch (e: Exception) {
            loggerApi.logEWithTag(LOG_TAG, "Error during $action", e)
        }
    }
}
