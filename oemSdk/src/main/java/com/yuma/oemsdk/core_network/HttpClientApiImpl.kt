package com.yuma.oemsdk.core_network

import android.util.Log
import com.yuma.oemsdk.Environment
import com.yuma.oemsdk.YumaSdk
import com.yumacustomer.core_logger.api.LoggerApi
import com.yumaoem.core.model.auth.AuthBearerTokens
import com.yumaoem.core_network.api.HttpClientApi
import com.yumaoem.core_network.impl.data.base.ApiResponse
import com.yumaoem.core_network.impl.model.RefreshTokenRequestBody
import com.yumaoem.core_network.impl.model.RefreshTokenResponseDTO
import com.yumaoem.core_network.impl.util.NetworkApiEvent
import com.yumaoem.core_network.impl.util.NetworkConstants.Endpoints
import com.yumaoem.core_network.impl.util.NetworkConstants.NetworkApiConfig
import com.yumaoem.core_network.impl.util.NetworkEventBus
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.host
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json

/**
 * Implementation of [HttpClientApi] that provides configured Ktor HTTP clients.
 *
 * This class is responsible for creating and configuring [HttpClient] instances for making network requests.
 * It handles different environments (DEV, PREPROD, PROD) to set the appropriate base URL.
 * It also sets up various Ktor plugins like authentication, logging, timeouts, and content negotiation.
 *
 * @param shouldEnableLogging Flag to enable or disable network logging.
 * @param json The [Json] instance for serialization/deserialization.
 * @param loggerApi The [LoggerApi] for logging.
 * @param preferenceUtilApi The [YumaPrefUtilApi] for accessing stored preferences like authentication tokens.
 * @param environment The target environment (e.g., "DEV", "PREPROD", "PROD"). Defaults to "PROD".
 */


class HttpClientApiImpl(
    private val shouldEnableLogging: Boolean,
    private val json: Json,
    private val loggerApi: LoggerApi,
    private val preferenceUtilApi: YumaPrefUtilApi,
    environment: Environment
) : HttpClientApi {
    private val TAG = HttpClientApiImpl::class.java.simpleName
    /**
     * The base URL for network requests, determined by the provided [environment].
     */
    val BASE_URL = when (environment) {
        Environment.DEV -> "dev3-backend-oem.yumax.app"
        Environment.PREPROD -> "preprod-backend-oem.yumax.app"
        Environment.PROD -> "backend-oem.yumax.app"
    }

    private val httpEngineProvider by lazy { HttpEngineProvider() }

    private val clientMutex = Mutex()

    private var authenticatedClient: HttpClient? = null

    private var onboardingClient: HttpClient? = null

    private fun createAuthenticatedClient(): HttpClient {
        return HttpClient(httpEngineProvider.clientEngine())
        {
            expectSuccess = true

            // Default request setup
            defaultRequest {
                host = BASE_URL
                url { protocol = URLProtocol.HTTPS }
                headers.append("x-client-id", "${YumaSdk.getConfig().clientId}")
                headers.append("x-client-secret", YumaSdk.getConfig().clientSecret)
                headers.append("Content-Type", "application/json")
                contentType(ContentType.Application.Json)
            }

            // Token Handling (no static token here, auth plugin manages it)
            install(Auth) {

                bearer {
                    loadTokens {
                        preferenceUtilApi.getBearerTokens()?.let {
                            BearerTokens(it.accessToken, it.refreshToken)
                        }
                    }
                    refreshTokens {
                        try {
                            loggerApi.logDWithTag("KtorAuth", "🔁 Refreshing tokens...")

                            val oldTokens = preferenceUtilApi.getBearerTokens()
                            val refreshToken = oldTokens?.refreshToken.orEmpty()

                            val response = client.post {
                                host = BASE_URL
                                url(Endpoints.REFRESH_TOKEN)
                                setBody(RefreshTokenRequestBody(refreshToken))
                                markAsRefreshTokenRequest()
                            }

                            val newTokens =
                                response.body<ApiResponse<RefreshTokenResponseDTO?>>().data
                            val access = newTokens?.accessToken?.token
                            val refresh = newTokens?.refreshToken?.token

                            if (access != null && refresh != null) {
                                preferenceUtilApi.clearBearerTokens()
                                preferenceUtilApi.saveBearerTokens(
                                    AuthBearerTokens(
                                        access,
                                        refresh
                                    )
                                )

                                loggerApi.logDWithTag("KtorAuth", "✅ Tokens refreshed and saved")

                                return@refreshTokens BearerTokens(access, refresh)
                            } else null

                        } catch (e: Exception) {
                            NetworkEventBus.INSTANCE.invokeEvent(NetworkApiEvent.REFRESH_TOKEN_EXPIRED)
                            loggerApi.logDWithTag("KtorAuth", "🔥 Refresh failed: $e")
                            null
                        }
                    }
                }
            }

            install(HttpTimeout) {
                requestTimeoutMillis = NetworkApiConfig.REQUEST_TIMEOUT_MILLIS
                socketTimeoutMillis = NetworkApiConfig.SOCKET_TIMEOUT_MILLIS
                connectTimeoutMillis = NetworkApiConfig.CONNECT_TIMEOUT_MILLIS
            }

            if (shouldEnableLogging) install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        loggerApi.logDWithTag(NetworkApiConfig.DEFAULT_LOG_TAG, message)
                        Log.d(TAG, message)
                    }
                }
                level = LogLevel.BODY
            }

            if (shouldEnableLogging) install(ResponseObserver) {
                onResponse {
                    loggerApi.logDWithTag(NetworkApiConfig.DEFAULT_LOG_TAG, it.bodyAsText())
                }
            }

            install(ContentNegotiation) { json(json) }

            // ── Network Inspection (Safety Abstracted) ──────────────────────────
            if (shouldEnableLogging)
                SdkNetworkInspector.create().install(this,)
        }
    }

    private fun createOnboardingClient(): HttpClient {
        return HttpClient(httpEngineProvider.clientEngine()) {
            expectSuccess = true
            defaultRequest {
                host = BASE_URL
                url { protocol = URLProtocol.HTTPS }
                contentType(ContentType.Application.Json)
            }
            install(HttpTimeout) {
                requestTimeoutMillis = NetworkApiConfig.REQUEST_TIMEOUT_MILLIS
                socketTimeoutMillis = NetworkApiConfig.SOCKET_TIMEOUT_MILLIS
                connectTimeoutMillis = NetworkApiConfig.CONNECT_TIMEOUT_MILLIS
            }

            if (shouldEnableLogging) install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        loggerApi.logDWithTag("OnboardingClient", message)
                    }
                }
                level = LogLevel.ALL
            }

            if (shouldEnableLogging) install(ResponseObserver) {
                onResponse {
                    loggerApi.logDWithTag("OnboardingClient", it.bodyAsText())
                }
            }

            install(ContentNegotiation) { json(json) }
            // ── Network Inspection (Safety Abstracted) ──────────────────────────
            if (shouldEnableLogging)
                SdkNetworkInspector.create().install(this)
        }
    }

    override suspend fun getAuthenticatedHttpClient(): HttpClient {
        clientMutex.withLock {
            if (authenticatedClient == null) {
                authenticatedClient = createAuthenticatedClient()
            }
            return authenticatedClient!!
        }
    }

    override suspend fun getOnboardingHttpClient(): HttpClient {
        clientMutex.withLock {
            if (onboardingClient == null) {
                onboardingClient = createOnboardingClient()
            }
            return onboardingClient!!
        }
    }

    override fun resetAuthTokens() {
        preferenceUtilApi.clearBearerTokens()
        loggerApi.logDWithTag("KtorClientApi", "🚪 Tokens cleared manually")
    }

    override fun resetKtorClients() {
        preferenceUtilApi.clearBearerTokens()
        authenticatedClient?.close()
        onboardingClient?.close()
        authenticatedClient = null
        onboardingClient = null

        loggerApi.logDWithTag(
            "KtorClientApi",
            "🔄 Clients reset, new instance will be created on next request"
        )
    }
}
