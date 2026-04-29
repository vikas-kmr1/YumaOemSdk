package com.yuma.oemsdk.network

import android.content.Context
import com.yuma.oemsdk.core_network.SdkNetworkInspector
import io.ktor.client.HttpClientConfig
import sp.bvantur.inspektify.ktor.InspektifyKtor

/**
 * Debug implementation of the network inspector using Inspektify.
 */
internal class SdkNetworkInspectorImpl : SdkNetworkInspector {
    
    override fun initialize(context: Context) {
        // Inspektify doesn't strictly need a separate manual initialization for Ktor,
        // but we could configure global settings here if needed.
    }

    override fun install(config: HttpClientConfig<*>) {
        config.install(InspektifyKtor) {
            // Optional: Configure redaction or data retention
            // redactHeader("Authorization")
        }
    }
}
