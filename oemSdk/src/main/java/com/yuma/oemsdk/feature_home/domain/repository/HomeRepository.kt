package com.yumaoem.feature_home.domain.repository

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.data.dto.GenericSuccessResponseDto
import com.yumaoem.feature_home.data.dto.auto_dialer.AutoDialerRequest
import com.yumaoem.feature_home.data.dto.auto_dialer.AutoDialerResponse
import com.yumaoem.feature_home.data.dto.beacon_details.request.BeaconDetailsRequest
import com.yumaoem.feature_home.data.dto.book_token.request.BookTokenRequest
import com.yumaoem.feature_home.data.dto.maps_screen.all_stations.request.AllStationsRequest
import com.yumaoem.feature_home.data.dto.start_diy_swap.StartDiySwapRequestDto
import com.yumaoem.feature_home.data.dto.start_diy_swap.StartDiySwapResponseDTO
import com.yumaoem.feature_home.data.dto.station_directions.request.RouteInfoRequest
import com.yumaoem.feature_home.data.dto.station_operation_status.request.StationOperationStatusRequest
import com.yumaoem.feature_home.data.dto.swap_history.request.SwapHistoryRequest
import com.yumaoem.feature_home.data.dto.whatsapp_support_details.SupportDetailsRequestDto
import com.yumaoem.feature_home.data.dto.tag_battery.request.TagBatteryRequestDTO
import com.yumaoem.feature_home.data.dto.check_in_user.location_validation.LocationValidationRequestDTO
import com.yumaoem.feature_home.data.dto.check_in_user.location_validation.LocationValidationResponseDTO
import com.yumaoem.feature_home.data.dto.logout.LogoutResponseDTO
import com.yumaoem.feature_home.data.dto.logout.LogoutUserRequestDTO
import com.yumaoem.feature_home.data.dto.revert_token_checkin.RevertTokenCheckInResponseDTO
import com.yumaoem.feature_home.data.dto.verify_batteries.request.VerifyBatteriesDTO
import com.yumaoem.feature_home.domain.model.drop_off_data.DropOffScreenData
import com.yumaoem.feature_home.domain.model.maps.all_station_markers.YumaStationMarker
import com.yumaoem.feature_home.domain.model.maps.all_station_markers.YumaStationStatus
import com.yumaoem.feature_home.domain.model.maps.stationRoute.RouteInfo
import com.yumaoem.feature_home.domain.model.profile.swap_history.SwapHistoryItem
import com.yumaoem.feature_home.domain.model.supportDetails.SupportDetails
import com.yumaoem.feature_home.domain.model.token_flow.battery_details.BatteryDetails
import com.yumaoem.feature_home.domain.model.token_flow.beacon_details.Beacon
import com.yumaoem.feature_home.domain.model.token_flow.book_token.BookedTokenDetails
import com.yumaoem.feature_home.domain.model.token_flow.cancel_token_booking.CancelTokenResponse
import com.yumaoem.feature_home.domain.model.token_flow.check_in.CheckInResponse
import com.yumaoem.feature_home.domain.model.token_flow.token_status.TokenStatus
import com.yumaoem.feature_home.domain.model.token_flow.verify_batteries.VerifyBatteries
import kotlinx.coroutines.flow.Flow

interface HomeRepository {

    suspend fun getBeaconDetails(
        request: BeaconDetailsRequest
    ): Flow<RestClientResult<List<Beacon>>>

    suspend fun bookToken(
        request: BookTokenRequest
    ):  Flow<RestClientResult<BookedTokenDetails>>


    suspend fun getAllStations(
        request: AllStationsRequest
    ): Flow<RestClientResult<List<YumaStationMarker>>>

    suspend fun getRouteInfo(
        routeInfoRequest: RouteInfoRequest
    ):Flow<RestClientResult<RouteInfo?>>

    suspend fun getStationStatus(
        stationStatusRequest: StationOperationStatusRequest
    ) : Flow<RestClientResult<YumaStationStatus>>

    suspend fun checkInUser(
        tokenId:Int
    ):Flow<RestClientResult<CheckInResponse>>

    suspend fun cancelTokenBooking(
        tokenId:Int
    ):Flow<RestClientResult<CancelTokenResponse>>

    suspend fun getTokenStatus(
        tokenId: Int
    ):Flow<RestClientResult<TokenStatus>>

    suspend fun getDropOffScreen(
        clientUserId:Int
    ):Flow<RestClientResult<DropOffScreenData>>

    suspend fun getBatteryDetails(
        clientVehicleId:Int
    ) : Flow<RestClientResult<List<BatteryDetails>>>

    suspend fun getSwapHistory(
        swapHistoryRequest: SwapHistoryRequest
    ):RestClientResult<List<SwapHistoryItem>>

    suspend fun getSupportDetails(requestDto: SupportDetailsRequestDto): Flow<RestClientResult<SupportDetails>>

    suspend fun validateLocation(
        request: LocationValidationRequestDTO
    ): Flow<RestClientResult<LocationValidationResponseDTO>>

    suspend fun startDiySwap(request: StartDiySwapRequestDto): Flow<RestClientResult<StartDiySwapResponseDTO>>

    suspend fun revertTokenCheckIn(tokenId:Int): Flow<RestClientResult<RevertTokenCheckInResponseDTO>>

    suspend fun logoutUser(requestDTO: LogoutUserRequestDTO): Flow<RestClientResult<LogoutResponseDTO>>

    suspend fun autoDialerRequest(requestDTO: AutoDialerRequest): Flow<RestClientResult<AutoDialerResponse>>

    suspend fun mapNewBatteriesOnBike(
        request: TagBatteryRequestDTO
    ): Flow<RestClientResult<GenericSuccessResponseDto>>

    suspend fun verifyBatteries(
        request: VerifyBatteriesDTO
    ): Flow<RestClientResult<VerifyBatteries>>
}