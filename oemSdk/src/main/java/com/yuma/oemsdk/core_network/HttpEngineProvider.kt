package com.yuma.oemsdk.core_network

import io.ktor.client.engine.android.Android

class HttpEngineProvider {
    fun clientEngine(): Android {
        return Android
    }
}
