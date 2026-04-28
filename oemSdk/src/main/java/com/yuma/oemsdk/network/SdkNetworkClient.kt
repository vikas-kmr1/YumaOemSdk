package com.yuma.oemsdk.network

import android.util.Log
import com.yuma.oemsdk.core_network.SdkNetworkInspector
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

private const val TAG = "SdkNetworkClient"

/**
 * Builds and holds the Ktor [HttpClient] for the Yuma OEM SDK.
 *
 * Uses the native Android engine (HttpURLConnection) — **no OkHttp dependency** —
 * to prevent version conflicts when integrated into a host app.
 *
 * @param baseUrl      The backend host (e.g. "backend-oem.yumax.app").
 * @param enableLogging Whether to print request/response logs (set false for release).
 * @param tokenProvider A lambda that returns the current [BearerTokens] for authenticated calls.
 *                      Return null if no token is available yet.
 * @param onTokenRefreshFailed Called when token refresh fails (e.g. to trigger re-init or notify host app).
 */
internal class SdkNetworkClient(
    private val baseUrl: String,
    private val enableLogging: Boolean,
    private val tokenProvider: suspend () -> BearerTokens?,
    private val onTokenRefreshFailed: () -> Unit = {}
) {
    /**
     * A lazily created [HttpClient] configured with:
     *  - Android engine (no OkHttp)
     *  - Bearer auth with auto-refresh hook
     *  - JSON content negotiation
     *  - Optional logging
     */
    val httpClient: HttpClient by lazy { buildClient() }

    private fun buildClient(): HttpClient {
        return HttpClient(Android) {
            expectSuccess = true

            // ── Base URL ──────────────────────────────────────────────────────────
            defaultRequest {
                host = baseUrl
                url { protocol = URLProtocol.HTTPS }
                contentType(ContentType.Application.Json)
            }

            // ── Bearer Auth ───────────────────────────────────────────────────────
            install(Auth) {
                bearer {
                    loadTokens {
                        tokenProvider()
                    }
                    refreshTokens {
                        // TODO: implement your token-refresh network call here.
                        // Return new BearerTokens on success, or null to trigger sign-out.
                        Log.w(TAG, "Token refresh needed — implement refreshTokens()")
                        onTokenRefreshFailed()
                        null
                    }
                }
            }

            // ── JSON Serialization ────────────────────────────────────────────────
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        encodeDefaults = true
                    }
                )
            }
            // ── Network Inspection (Safety Abstracted) ──────────────────────────
            SdkNetworkInspector.create().install(this)
            // ── Logging (disabled in release automatically) ───────────────────────
            if (enableLogging) {
                install(Logging) {
                    logger = object : Logger {
                        override fun log(message: String) {
                            Log.d(TAG, message)
                        }
                    }
                    level = LogLevel.BODY
                }
            }


        }
    }

    /** Closes the underlying HTTP client. Call this on SDK reset / logout. */
    fun close() {
        if (httpClient.isActive) {
            httpClient.close()
            Log.d(TAG, "HttpClient closed")
        }
    }
}
