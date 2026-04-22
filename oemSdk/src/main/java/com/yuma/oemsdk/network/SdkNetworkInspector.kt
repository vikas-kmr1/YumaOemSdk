package com.yuma.oemsdk.network

import android.content.Context
import io.ktor.client.HttpClientConfig

/**
 * Interface for the SDK network inspector.
 * This is abstracted so that the real implementation (Inspektify) can be
 * linked only in debug builds, ensuring production builds have zero overhead.
 */
internal interface SdkNetworkInspector {
    fun initialize(context: Context)
    fun install(config: HttpClientConfig<*>)

    companion object {
        /**
         * Returns the build-type specific inspector instance.
         * The Actual implementation is found in src/debug and src/release.
         */
        fun create(): SdkNetworkInspector = SdkNetworkInspectorImpl()
    }
}
