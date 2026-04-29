package com.yumacustomer.core_analytics.api

import kotlinx.serialization.json.JsonObject


/**
 * Cross-platform analytics API interface
 */
interface AnalyticsApi {

    /**
     * Post event without any data
     */
    suspend fun postEvent(eventName: String)

    /**
     * Post event with single value without key
     */
    suspend fun postEvent(event: String, value: String)

    /**
     * Post event with single key-value pair
     */
    suspend fun postEvent(event: String, key: String, value: String)

    /**
     * Post event with map data
     */
    suspend fun postEvent(event: String, values: Map<String, Any>)

    /**
     * Post event with JsonObject data
     */
    suspend fun postEvent(event: String, properties: JsonObject)

    /**
     * Identify user with traits
     */
    suspend fun identify(userId: String, traits: Map<String, Any> = emptyMap())

    /**
     * Set user properties
     */
    suspend fun setUserProperties(properties: Map<String, Any>)

    /**
     * Flush pending events
     */
    suspend fun flush()

    /**
     * Must be called when app is being destroyed
     */
    suspend fun tearDown()

    /**
     * Enable/disable analytics at runtime
     */
    fun setAnalyticsEnabled(enabled: Boolean)

    /**
     * Clears the currently identified user and any associated traits
     * from the analytics system.
     *
     * This should be called when the user logs out or their session expires,
     * ensuring that subsequent events are not attributed to the previous user.
     *
     * Typically followed by a fresh `identify()` call after a new user logs in.
     */
    suspend fun resetUser()
}
