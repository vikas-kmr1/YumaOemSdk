package com.yumaoem.feature_home.data.network

import com.yuma.oemsdk.YumaSdk
import com.yumaoem.core.utils.core_locaction_prodvider.CoreLocationProvider
import com.yumaoem.core_network.api.HttpClientApi
import com.yumaoem.core_network.impl.data.base.BaseDataSource
import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.data.dto.GetBatteryDetails.BatteryDetailsResponseDto
import com.yumaoem.feature_home.data.dto.auto_dialer.AutoDialerRequest
import com.yumaoem.feature_home.data.dto.auto_dialer.AutoDialerResponse
import com.yumaoem.feature_home.data.dto.beacon_details.request.BeaconDetailsRequest
import com.yumaoem.feature_home.data.dto.beacon_details.response.BeaconResponseDTO
import com.yumaoem.feature_home.data.dto.book_token.request.BookTokenRequest
import com.yumaoem.feature_home.data.dto.book_token.response.BookTokenResponseDTO
import com.yumaoem.feature_home.data.dto.cancel_token_booking.response.CancelTokenBookingResponseDTO
import com.yumaoem.feature_home.data.dto.check_in_user.CheckInResponseDTO
import com.yumaoem.feature_home.data.dto.check_in_user.location_validation.LocationValidationRequestDTO
import com.yumaoem.feature_home.data.dto.check_in_user.location_validation.LocationValidationResponseDTO
import com.yumaoem.feature_home.data.dto.drop_off_screen.response.DropOffScreenResponseDTO
import com.yumaoem.feature_home.data.dto.fcm_token.request.RemoveFcmTokenRequestDto
import com.yumaoem.feature_home.data.dto.logout.LogoutResponseDTO
import com.yumaoem.feature_home.data.dto.logout.LogoutUserRequestDTO
import com.yumaoem.feature_home.data.dto.maps_screen.all_stations.NearbyStationsResponseDTO
import com.yumaoem.feature_home.data.dto.maps_screen.all_stations.OperationStatus
import com.yumaoem.feature_home.data.dto.maps_screen.all_stations.request.AllStationsRequest
import com.yumaoem.feature_home.data.dto.payment_plans.PaymentPlansResponseDTO
import com.yumaoem.feature_home.data.dto.payment_plans.create_order.CreateOrderRequestDTO
import com.yumaoem.feature_home.data.dto.payment_plans.create_order.CreateOrderResponseDTO
import com.yumaoem.feature_home.data.dto.payment_plans.payment_status.PaymentStatusRequestDTO
import com.yumaoem.feature_home.data.dto.payment_plans.payment_status.PaymentStatusResponseDTO
import com.yumaoem.feature_home.data.dto.revert_token_checkin.RevertTokenCheckInResponseDTO
import com.yumaoem.feature_home.data.dto.start_diy_swap.StartDiySwapRequestDto
import com.yumaoem.feature_home.data.dto.start_diy_swap.StartDiySwapResponseDTO
import com.yumaoem.feature_home.data.dto.station_directions.DirectionsResponseDTO
import com.yumaoem.feature_home.data.dto.station_directions.request.RouteInfoRequest
import com.yumaoem.feature_home.data.dto.station_operation_status.request.StationOperationStatusRequest
import com.yumaoem.feature_home.data.dto.swap_history.request.SwapHistoryRequest
import com.yumaoem.feature_home.data.dto.swap_history.response.SwapHistoryResponseDTO
import com.yumaoem.feature_home.data.dto.token_status.response.TokenStatusResponseDTO
import com.yumaoem.feature_home.data.dto.whatsapp_support_details.SupportDetailsDTO
import com.yumaoem.feature_home.data.dto.tag_battery.request.TagBatteryRequestDTO
import com.yumaoem.feature_home.data.dto.GenericSuccessResponseDto
import com.yumaoem.feature_home.data.dto.verify_batteries.request.VerifyBatteriesDTO
import com.yumaoem.feature_home.data.dto.whatsapp_support_details.SupportDetailsRequestDto
import com.yumaoem.feature_home.data.network.util.Endpoints
import com.yumaoem.feature_home.data.network.util.Endpoints.CREATE_ORDER
import com.yumaoem.feature_home.data.network.util.Endpoints.GET_ALL_PLANS
import com.yumaoem.feature_home.data.network.util.Endpoints.LOGOUT_USER
import com.yumaoem.feature_home.data.network.util.Endpoints.MAKE_CALL
import com.yumaoem.feature_home.data.network.util.Endpoints.PAYMENT_STATUS
import com.yumaoem.feature_home.data.network.util.Endpoints.REVERT_TOKEN_CHECK_IN
import com.yumaoem.feature_home.data.network.util.Endpoints.ROUTE_BY_ROAD
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json

class HomeRemoteDataSource (
    private val httpClientApi: HttpClientApi,
    private val json: Json,
    private val coreLocationProvider: CoreLocationProvider
): BaseDataSource() {

    suspend fun getBeaconDetails(
        request: BeaconDetailsRequest
    ) = getResult<BeaconResponseDTO> {
        val client = httpClientApi.getAuthenticatedHttpClient()
        client.get {
            url(Endpoints.BEACON_V2)
            parameter("latitude", request.latitude)
            parameter("longitude", request.longitude)
            parameter("radius",100)
            parameter("tokenId",request.tokenId)
        }
    }

    suspend fun bookToken(
        request: BookTokenRequest
    ) = getResult<BookTokenResponseDTO> {
        val client = httpClientApi.getAuthenticatedHttpClient()
        client.post {
            url(Endpoints.BOOK_TOKEN)
            setBody(request)
        }
    }

    suspend fun getAllStations(
        request: AllStationsRequest
    ) = getResult<NearbyStationsResponseDTO> {
        val client = httpClientApi.getAuthenticatedHttpClient()
        client.get {
            url(Endpoints.NEARBY_STATIONS)
            parameter("latitude", request.latitude)
            parameter("longitude", request.longitude)
            parameter("clientCityId",request.clientCityId)
            parameter("clientVehicleId",request.clientVehicleId)
            parameter("radius",50000)
            parameter("clientUserId",request.userId)
        }
    }

    suspend fun getRouteByRoad(
        routeInfoRequest: RouteInfoRequest,
    ): RestClientResult<DirectionsResponseDTO>  {
        val apiKey = YumaSdk.getConfig().mapApiKey
        val originStr = "${routeInfoRequest.origin.latitude},${routeInfoRequest.origin.longitude}"
        val destinationStr = "${routeInfoRequest.destination.latitude},${routeInfoRequest.destination.longitude}"
        val client = httpClientApi.getAuthenticatedHttpClient()
        try {
            val response: HttpResponse =  client.get {
                url(ROUTE_BY_ROAD)
                parameter("origin", originStr)
                parameter("destination", destinationStr)
                parameter("key", apiKey)
            }
            val body = response.bodyAsText()
            val directions = json.decodeFromString<DirectionsResponseDTO>(body)
            return (RestClientResult.success(directions))
        }catch (e: Exception){
            return (RestClientResult.error(e.message.toString()))
        }
    }

    suspend fun getStationOperationStatus(
        stationOperationStatusRequest: StationOperationStatusRequest
    ) = getResult<OperationStatus> {
        val client = httpClientApi.getAuthenticatedHttpClient()
        client.get(Endpoints.STATION_OPERATION_STATUS){
            parameter("csId", stationOperationStatusRequest.csId)
            parameter("clientCityId", stationOperationStatusRequest.clientCityId)
            parameter("clientVehicleId",stationOperationStatusRequest.clientVehicleId)
        }
    }

    suspend fun checkInUser(
        tokenId:Int
    ) = getResult<CheckInResponseDTO> {
        val client = httpClientApi.getAuthenticatedHttpClient()
        client.put("${Endpoints.CHECK_IN_AT_STATION}/${tokenId}/check-in")
    }

    suspend fun cancelToken(
        tokenId: Int
    ) = getResult<CancelTokenBookingResponseDTO> {
        val client = httpClientApi.getAuthenticatedHttpClient()
        client.put("${Endpoints.CANCEL_TOKEN}/${tokenId}/cancel")
    }

    suspend fun getTokenStatus(
        tokenId: Int,
        clientSecret: String
    ) = getResult<TokenStatusResponseDTO> {
        val client = httpClientApi.getAuthenticatedHttpClient()
        client.get{
            url(Endpoints.TOKEN_STATUS)
            parameter("tokenId", tokenId)
            header("Authorization", "Bearer $clientSecret")
        }
    }

    suspend fun getDropOffScreen(
        clientVehicleId:Int
    ) = getResult<DropOffScreenResponseDTO> {
        val client = httpClientApi.getAuthenticatedHttpClient()
        client.get {
            url(Endpoints.DROP_OFF_SCREEN)
            parameter("clientVehicleId", clientVehicleId)
        }
    }

    suspend fun getBatteryDetails(
        clientVehicleId:Int
    ) = getResult<List<BatteryDetailsResponseDto>> {
        val client = httpClientApi.getAuthenticatedHttpClient()
        client.get{
            url(Endpoints.GET_CURRENT_BATTERY_DETAILS)
            parameter("client_vehicle_id",clientVehicleId)
        }
    }

    suspend fun getSwapHistory(
        swapHistoryRequest: SwapHistoryRequest
    ) = getResult<SwapHistoryResponseDTO> {
        val client = httpClientApi.getAuthenticatedHttpClient()
        client.post {
            url(Endpoints.GET_SWAP_HISTORY)
            setBody(swapHistoryRequest)
        }
    }

    suspend fun getSupportDetails(request: SupportDetailsRequestDto) = getResult<SupportDetailsDTO> {
        val client = httpClientApi.getAuthenticatedHttpClient()
        client.get {
            url(Endpoints.WHATSAPP_HELP)
            parameter("clientUserId", request.clientUserId)
            parameter("clientVehicleId", request.clientVehicleId)
            parameter("latitude", request.latitude)
            parameter("longitude", request.longitude)
        }
    }


    suspend fun removeFCMToken(
        request: RemoveFcmTokenRequestDto
    ) = getResult<Any> {
        val client = httpClientApi.getAuthenticatedHttpClient()
        client.post {
            url(Endpoints.REMOVE_FCM_TOKEN)
            setBody(request)
        }
    }

    suspend fun validateLocation(
        request: LocationValidationRequestDTO
    ) = getResult<LocationValidationResponseDTO> {
        val location = coreLocationProvider.getCurrentLocation()
        val client = httpClientApi.getAuthenticatedHttpClient()
        client.post {
            url(Endpoints.VALIDATE_LOCATION)
            parameter("latitude", location?.latitude)
            parameter("longitude", location?.longitude)
            parameter("radius",100)
            parameter("tokenId",request.tokenId)
            setBody(request)
        }
    }

    suspend fun revertTokenCheckIn(
        tokenId: Int
    ) = getResult<RevertTokenCheckInResponseDTO> {
        val client = httpClientApi.getAuthenticatedHttpClient()
        client.put {
            url(urlString = REVERT_TOKEN_CHECK_IN(tokenId))
        }
    }

    suspend fun logoutUser(
        request: LogoutUserRequestDTO
    ): RestClientResult<LogoutResponseDTO> {
        val result = getResult<LogoutResponseDTO> {
            val client = httpClientApi.getAuthenticatedHttpClient()
            client.post {
                url(urlString = LOGOUT_USER)
                setBody(request)
            }
        }

        if (result.status == RestClientResult.Status.SUCCESS) {
            httpClientApi.resetKtorClients()
        }

        return result
    }



    suspend fun requestCustomerSupportCall(
        request: AutoDialerRequest
    ) = getResult<AutoDialerResponse> {
        val client = httpClientApi.getAuthenticatedHttpClient()
        client.post {
            url(urlString = MAKE_CALL)
            setBody(request)
        }
    }

    suspend fun getPaymentPlans(clientVehicleId: String) = getResult<PaymentPlansResponseDTO> {
        val client = httpClientApi.getAuthenticatedHttpClient()
        client.get {
            url(urlString = GET_ALL_PLANS)
            parameter("client_vehicle_id", clientVehicleId)
        }
    }


    suspend fun getPaymentStatus(request: PaymentStatusRequestDTO) = getResult<PaymentStatusResponseDTO> {
        val client = httpClientApi.getAuthenticatedHttpClient()
        client.post {
            url(urlString = PAYMENT_STATUS)
            setBody(request)
        }
    }

    suspend fun createOrder(request: CreateOrderRequestDTO) = getResult<CreateOrderResponseDTO> {
        val client = httpClientApi.getAuthenticatedHttpClient()
        client.post {
            url(urlString = CREATE_ORDER)
            setBody(request)
        }
    }

    suspend fun verifyBatteryDetails(request: VerifyBatteriesDTO) = getResult<Boolean> {
        val client = httpClientApi.getAuthenticatedHttpClient()
        client.post {
            url(Endpoints.VERIFY_BATTERIES)
            setBody(request)
        }
    }
}