package com.yuma.oemsdk.data.network

import android.util.Log
import com.yuma.oemsdk.data.dto.SdkAllStationsRequestDto
import com.yuma.oemsdk.data.dto.SdkBatteryDetailsDto
import com.yuma.oemsdk.data.dto.SdkBookTokenRequestDto
import com.yuma.oemsdk.data.dto.SdkBookTokenResponseDto
import com.yuma.oemsdk.data.dto.SdkCreateOrderRequestDto
import com.yuma.oemsdk.data.dto.SdkCreateOrderResponseDto
import com.yuma.oemsdk.data.dto.SdkGenericSuccessDto
import com.yuma.oemsdk.data.dto.SdkLogoutRequestDto
import com.yuma.oemsdk.data.dto.SdkNearbyStationsResponseDto
import com.yuma.oemsdk.data.dto.SdkPaymentPlansResponseDto
import com.yuma.oemsdk.data.dto.SdkRemoveFcmTokenDto
import com.yuma.oemsdk.data.dto.SdkStationDto
import com.yuma.oemsdk.data.dto.SdkSupportDetailsDto
import com.yuma.oemsdk.data.dto.SdkSwapHistoryRequestDto
import com.yuma.oemsdk.data.dto.SdkSwapHistoryResponseDto
import com.yuma.oemsdk.data.dto.SdkTokenStatusResponseDto
import com.yuma.oemsdk.network.SdkNetworkClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

private const val TAG = "SdkHomeRemoteDS"

/**
 * SDK-internal data source for all feature-home API calls.
 *
 * Mirrors [HomeRemoteDataSource] from the OEM app but uses [SdkNetworkClient]
 * directly — no Koin, no KMP, no OkHttp dependency.
 *
 * All methods are `suspend` and return [SdkResult] which wraps success/error.
 */
internal class SdkHomeRemoteDataSource(private val networkClient: SdkNetworkClient) {

    private val client get() = networkClient.httpClient

    // ── Stations ──────────────────────────────────────────────────────────────

    suspend fun getNearbyStations(request: SdkAllStationsRequestDto): SdkResult<SdkNearbyStationsResponseDto> =
        safeCall {
            val resposne:SdkNearbyStationsResponseDto = client.get {
                url(SdkEndpoints.NEARBY_STATIONS)
                parameter("latitude",        request.latitude)
                parameter("longitude",       request.longitude)
                parameter("clientCityId",    request.clientCityId)
                parameter("clientVehicleId", request.clientVehicleId)
                parameter("radius",          50_000)
                parameter("clientUserId",    request.userId)
            }.body()

            Log.d(TAG, "getNearbyStations: $request")
            resposne
        }

    // ── Token Booking ─────────────────────────────────────────────────────────

    suspend fun bookToken(request: SdkBookTokenRequestDto): SdkResult<SdkBookTokenResponseDto> =
        safeCall {
            client.post {
                url(SdkEndpoints.BOOK_TOKEN)
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        }

    suspend fun cancelToken(tokenId: Int): SdkResult<SdkGenericSuccessDto> =
        safeCall {
            client.put("${SdkEndpoints.CANCEL_TOKEN}/$tokenId/cancel").body()
        }

    suspend fun checkInUser(tokenId: Int): SdkResult<SdkGenericSuccessDto> =
        safeCall {
            client.put("${SdkEndpoints.CHECK_IN_AT_STATION}/$tokenId/check-in").body()
        }

    suspend fun revertTokenCheckIn(tokenId: Int): SdkResult<SdkGenericSuccessDto> =
        safeCall {
            client.put { url(SdkEndpoints.REVERT_TOKEN_CHECK_IN(tokenId)) }.body()
        }

    suspend fun getTokenStatus(tokenId: Int): SdkResult<SdkTokenStatusResponseDto> =
        safeCall {
            client.get {
                url(SdkEndpoints.TOKEN_STATUS)
                parameter("tokenId", tokenId)
            }.body()
        }

    // ── Profile ───────────────────────────────────────────────────────────────

    suspend fun getBatteryDetails(clientVehicleId: Int): SdkResult<List<SdkBatteryDetailsDto>> =
        safeCall {
            client.get {
                url(SdkEndpoints.GET_CURRENT_BATTERY_DETAILS)
                parameter("client_vehicle_id", clientVehicleId)
            }.body()
        }

    suspend fun getSwapHistory(request: SdkSwapHistoryRequestDto): SdkResult<SdkSwapHistoryResponseDto> =
        safeCall {
            client.post {
                url(SdkEndpoints.GET_SWAP_HISTORY)
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        }

    suspend fun logoutUser(request: SdkLogoutRequestDto): SdkResult<SdkGenericSuccessDto> =
        safeCall {
            client.post {
                url(SdkEndpoints.LOGOUT_USER)
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        }

    suspend fun removeFcmToken(request: SdkRemoveFcmTokenDto): SdkResult<SdkGenericSuccessDto> =
        safeCall {
            client.post {
                url(SdkEndpoints.REMOVE_FCM_TOKEN)
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        }

    // ── Support ───────────────────────────────────────────────────────────────

    suspend fun getSupportDetails(
        clientUserId: Int,
        clientVehicleId: Int,
        latitude: Double,
        longitude: Double
    ): SdkResult<SdkSupportDetailsDto> = safeCall {
        client.get {
            url(SdkEndpoints.WHATSAPP_HELP)
            parameter("clientUserId",    clientUserId)
            parameter("clientVehicleId", clientVehicleId)
            parameter("latitude",        latitude)
            parameter("longitude",       longitude)
        }.body()
    }

    // ── Payments ──────────────────────────────────────────────────────────────

    suspend fun getPaymentPlans(clientVehicleId: String): SdkResult<SdkPaymentPlansResponseDto> =
        safeCall {
            client.get {
                url(SdkEndpoints.GET_ALL_PLANS)
                parameter("client_vehicle_id", clientVehicleId)
            }.body()
        }

    suspend fun createOrder(request: SdkCreateOrderRequestDto): SdkResult<SdkCreateOrderResponseDto> =
        safeCall {
            client.post {
                url(SdkEndpoints.CREATE_ORDER)
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body()
        }

    // ── Safe Call Wrapper ─────────────────────────────────────────────────────

    private inline fun <reified T> safeCall(block: () -> T): SdkResult<T> {
        return try {
            SdkResult.Success(block())
        } catch (e: Exception) {
            Log.e(TAG, "API error: ${e.message}", e)
            SdkResult.Error(e.message ?: "Unknown error")
        }
    }
}

// ── Result wrapper ────────────────────────────────────────────────────────────

sealed class SdkResult<out T> {
    data class Success<T>(val data: T) : SdkResult<T>()
    data class Error(val message: String) : SdkResult<Nothing>()

    val isSuccess get() = this is Success
    fun getOrNull(): T? = (this as? Success)?.data
}
