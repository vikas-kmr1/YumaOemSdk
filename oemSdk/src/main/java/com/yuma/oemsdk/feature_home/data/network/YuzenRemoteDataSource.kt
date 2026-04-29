package com.yumaoem.feature_home.data.network

import com.yumaoem.core.utils.core_locaction_prodvider.CoreLocationProvider
import com.yumaoem.core_network.api.HttpClientApi
import com.yumaoem.core_network.impl.data.base.BaseDataSource
import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.core_network.impl.model.YuzenErrorResponseDTO
import com.yumaoem.feature_home.data.dto.GenericSuccessResponseDto
import com.yumaoem.feature_home.data.dto.start_diy_swap.StartDiySwapRequestDto
import com.yumaoem.feature_home.data.dto.start_diy_swap.StartDiySwapResponseDTO
import com.yumaoem.feature_home.data.dto.tag_battery.request.TagBatteryRequestDTO
import com.yumaoem.feature_home.data.network.util.Endpoints
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class YuzenRemoteDataSource(
    private val httpClientApi: HttpClientApi,
    private val json: Json,
    private val coreLocationProvider: CoreLocationProvider
) : BaseDataSource() {

    suspend fun startDiySwap(
        request: StartDiySwapRequestDto
    ): RestClientResult<StartDiySwapResponseDTO> {
        return try {
            val location = coreLocationProvider.getCurrentLocation()
            val client = httpClientApi.getAuthenticatedHttpClient()

            val response = client.post(
                "${Endpoints.YUZEN_DEV_BASE_URL}${Endpoints.START_DIY_SWAP}"
            ) {
                parameter("current_latitude", location?.latitude)
                parameter("current_longitude", location?.longitude)
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<StartDiySwapResponseDTO>()

            RestClientResult.success(response)

        } catch (e: ClientRequestException) {
            RestClientResult.error(
                extractErrorMessage(e),
                e.response.status.value
            )
        } catch (e: ServerResponseException) {
            RestClientResult.error(
                extractErrorMessage(e),
                e.response.status.value
            )
        } catch (e: Exception) {
            RestClientResult.error(e.message ?: "Unknown error")
        }
    }

    suspend fun mapNewBatteriesOnBike(
        request: TagBatteryRequestDTO
    ): RestClientResult<GenericSuccessResponseDto> {
        return try {
            val client = httpClientApi.getAuthenticatedHttpClient()

            val response = client.post(
                "${Endpoints.YUZEN_DEV_BASE_URL}${Endpoints.TAG_BATTERY}"
            ) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<GenericSuccessResponseDto>()

            RestClientResult.success(response)

        } catch (e: ClientRequestException) {
            RestClientResult.error(
                extractErrorMessage(e),
                e.response.status.value
            )
        } catch (e: ServerResponseException) {
            RestClientResult.error(
                extractErrorMessage(e),
                e.response.status.value
            )
        } catch (e: Exception) {
            RestClientResult.error(e.message ?: "Unknown error")
        }
    }


    private suspend fun extractErrorMessage(
        exception: ResponseException
    ): String {
        return try {
            val errorBody = exception.response.bodyAsText()
            val apiError = json.decodeFromString<YuzenErrorResponseDTO>(errorBody)
            apiError.message
        } catch (e: Exception) {
            exception.response.status.description
        }
    }
}
