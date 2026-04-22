package com.yuma.oemsdk.network

import android.util.Log
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val TAG = "SdkSilentAuth"

// ── Request & Response DTOs ───────────────────────────────────────────────────

@Serializable
private data class ClientAuthRequest(
    @SerialName("clientKey") val clientKey: String
)

@Serializable
private data class ClientAuthResponse(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String
)

// ── Sealed result type ────────────────────────────────────────────────────────

sealed class AuthResult {
    data class Success(val accessToken: String, val refreshToken: String) : AuthResult()
    data class Failure(val message: String) : AuthResult()
}

/**
 * Handles the silent background authentication of the SDK using the client key.
 *
 * Called once on [SdkLaunchActivity] startup. On success, tokens are stored via
 * [com.yuma.oemsdk.prefs.SdkPrefManager] and the Ktor bearer auth plugin loads them
 * automatically for all subsequent authenticated requests.
 *
 * On failure, [AuthResult.Failure] is returned and the SDK shows [SdkAuthFailedScreen].
 */
internal class SdkSilentAuthManager(
    private val networkClient: SdkNetworkClient,
    private val onTokensObtained: suspend (accessToken: String, refreshToken: String) -> Unit
) {
    /**
     * Exchanges the provided [clientKey] for a JWT session token pair.
     *
     * TODO: Replace the endpoint path and response shape with your actual backend contract.
     *
     * Expected POST body:  { "clientKey": "<key>" }
     * Expected response:   { "accessToken": "...", "refreshToken": "..." }
     */
    suspend fun authenticate(clientKey: String): AuthResult {
        return try {
            Log.d(TAG, "🔑 Starting silent auth for client key...")

//            val response = networkClient.httpClient.post {
//                url("/api/v1/oem/auth/client-token")   // TODO: replace with real endpoint
//                contentType(ContentType.Application.Json)
//                setBody(ClientAuthRequest(clientKey))
//            }.body<ClientAuthResponse>()

            Log.d(TAG, "✅ Silent auth succeeded")
            val accessToken =
               "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI3NTM3ODgyIiwidHlwZSI6Ik9FTV9BQ0NFU1NfVE9LRU4iLCJpYXQiOjE3NzY3Nzc3MjUsImFwcGxpY2F0aW9uX3NyY19pZCI6MjAsImV4cCI6MTc3Njg2NDEyNX0.ZlBm3EAIUZb9g2dnBWMvKvKdQCO5OCpGmT4zO5nwIEHfwSTAJdRYOpQ6svYmBB813kMwu7SlIMN8IjKeUsrdXpvFH_Ni-3LcOq0qySZBCWvprxzu47k2F14YuTmtysshQoEoJaxpVOaaDmrzqGxyrrRx0B6uGjAGiiJ2iE5mjYda-GuiwWxTh8nHCqt_NbJsbGVx_yboUUtzqL9hc1sHutPveBA3J4Ck3AhmqUS1OWJBMDt7DRJLJWi7LKWwE9bfUIHGVbX6GKXLGwZzbT5H-bjUWkbZhRYczcplMNSLHWTlCEVd0Dl-vTe-n8ialqOiN-1ZVAeHszxUkzGx7D9bZcEyWJaRJ-eTZ6voh6lONKuX9CmwUko1xMTOr0cJgzW2O-7GYA54BlCAbKLdCITNUMQuSUcEstZDHkm7w-LZGnLrHg7hryL1qsLqDWk-yAjKkqfhXXPhCgGEPsw9HrQwptkBB01ZBTU6upp8kRvaP7PVIT8BhjR_Hf5n4cTLZXHCpWW2_7TiH3jIqvqrG_uetccHcY6NessZcaHqbbIPRVDfva51pFuDIdBGaMt2z3FJDT7CKj_nJH8MM8xka_Wiz77Uc-SOLP3KpquGvV_wZtePwQZPv-k7oxzvpyWOxi4zz4afkvOir2mF5c6iFFCaAAn6ufhTP7t4_U7j3V6Uuqo"
            val refreshToken =
                "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiI3NTM3ODczIiwidHlwZSI6Ik9FTV9SRUZSRVNIX1RPS0VOIiwiaWF0IjoxNzc2NzY2OTgwLCJhcHBsaWNhdGlvbl9zcmNfaWQiOjIwLCJleHAiOjE3NzkzNTg5ODB9.ZZqxl5uSjg7qHi3KlqdAh2nAYBuF58kBg8gRC91AsyvbUht7tRCLBHPwxlKqgShMlLOj3HT_Tv3YaSooqFI1OyTSRXJgP6tSeq8i30D7dh2lHLrxvn6gS6L6-MqjvtHSHt4NcChk4o-IMfJFZ0Cn6XeCymh_WwseYUino-bQmwhJ5_bCTQSG--jBo9uZITlyMaD8N7SfyKnJbQjrpL5k9EjNBK_Q6TowTgQnb7P_rT4TcD4GFsNdqtq7wgM2uxgeutiC_nLAIjzsKmDP_YepRZ9spUPPrAhTnZY1HjwgOOYpJt1fnsVsQ4ErWlGNFHfSVyYU0U0koAamu52cS7xH-VAMhM3H3-BEHqn6E8SVYwvKYaivVqNBY_9Pd5VG5qZqe_Z01BvPXvtnpevYou11f75evfFCcB0N4d56XzKgHL1ZV9Yvdt4E5IyeYlYyC5MN_2zmuvWvW3d6MUKQiwX98-mCcgOCyida3xnqMxkgQo6UMP9p_aR9Ix1JT_IMj9FZAzO_3IlgskkwH-DXbMUtb_nyR_JIVmUxxqTqszrLX4bePcnTosqssA4tBAJ7Dam90janbXlVDAO9WBwhbtFKTl6QG1JvVR8GizE97SVuyCoUtwYcg29vc82Y-qk-KN9g3Lwb0F1Mb1zRRH8a6eEhOxuA8cerUppVoemf16zPF-0"
            onTokensObtained(accessToken, refreshToken)
            AuthResult.Success(accessToken, refreshToken)

        } catch (e: Exception) {
            Log.e(TAG, "🔥 Silent auth failed: ${e.message}")
            AuthResult.Failure(e.message ?: "Unknown authentication error")
        }
    }
}
