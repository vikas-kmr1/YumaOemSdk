package com.yumacustomer.core_analytics.impl.config


object SegmentConfigSettings {

    val JITSU_WRITE_KEY = "VbXlsLTskUzeBBXzi7GLqJdWlTCqnzyS:RAhhnIf9mDi8OYSSzj6GzNWODCHNkfFN" //TODO

    const val API_HOST = "jitsu-ingest.yumax.app"

    /**
     * The count of events at which Segment flushes events.
     */
    const val FLUSH_AT = 2

    /**
     * The interval in seconds at which Segment flushes events
     */
    const val FLUSH_INTERVAL = 30

    /**
     *  Tracks Lifecycle events -
     *  (Application Opened, Application Installed, Application Updated)
     */
    const val TRACK_LIFECYCLE_EVENTS = false
}