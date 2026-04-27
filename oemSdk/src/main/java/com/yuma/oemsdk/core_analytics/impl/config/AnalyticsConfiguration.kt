package com.yumacustomer.core_analytics.impl.config

/**
 * Configuration for analytics initialization
 */
data class AnalyticsConfiguration(
    val writeKey: String,
    val logTag: String = "YUMA_ANALYTICS_LOG",
    val enableEventLoggingInLogcat: Boolean = true,
    val isAnalyticsEnabled: Boolean = true,
    val platformContext: Any? = null // Android Context, iOS context, etc.
)