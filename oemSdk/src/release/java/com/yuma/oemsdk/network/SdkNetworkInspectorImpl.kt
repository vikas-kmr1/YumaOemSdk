package com.yuma.oemsdk.network

import android.content.Context
import io.ktor.client.HttpClientConfig
import com.yuma.oemsdk.core_network.SdkNetworkInspector

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


    companion object {
        /**
         * Returns the build-type specific inspector instance.
         * The Actual implementation is found in src/debug and src/release.
         */
        fun create(): SdkNetworkInspector = SdkNetworkInspectorImpl()
    }
}
