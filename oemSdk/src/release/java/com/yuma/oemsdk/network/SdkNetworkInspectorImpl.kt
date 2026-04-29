package com.yuma.oemsdk.network

import android.content.Context
import io.ktor.client.HttpClientConfig

/**
 * Release (No-Op) implementation of the network inspector.
 * This class is as empty as possible to ensure the compiler strips all
 * inspection logic from production builds.
 */
internal class SdkNetworkInspectorImpl : SdkNetworkInspector {
    override fun initialize(context: Context) {
        // No-Op
    }

    override fun install(config: HttpClientConfig<*>) {
        // No-Op
    }
}
