package com.yumaoem.core_network.api

import io.ktor.client.HttpClient


interface HttpClientApi {
    suspend fun getAuthenticatedHttpClient(): HttpClient
    suspend fun getOnboardingHttpClient(): HttpClient

    fun resetAuthTokens()
    fun resetKtorClients()
}