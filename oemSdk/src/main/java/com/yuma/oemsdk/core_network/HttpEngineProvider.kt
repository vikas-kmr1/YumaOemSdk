package com.yuma.oemsdk.core_network

import com.yumaoem.core.utils.context.AndroidContextProvider
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.android.Android
import okhttp3.OkHttpClient

class HttpEngineProvider constructor() {
    fun clientEngine(): Android {
        return Android
    }
}
