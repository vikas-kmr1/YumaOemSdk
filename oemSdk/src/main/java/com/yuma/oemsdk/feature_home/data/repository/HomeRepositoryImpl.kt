package com.yumaoem.feature_home.data.repository

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.core_network.impl.util.getFlowResult
import com.yumaoem.core_network.impl.util.mapFromDTO
import com.yumaoem.feature_home.data.dto.GenericSuccessResponseDto
import com.yumaoem.feature_home.data.dto.GetBatteryDetails.toDomain
import com.yumaoem.feature_home.data.dto.auto_dialer.AutoDialerRequest
import com.yumaoem.feature_home.data.dto.auto_dialer.AutoDialerResponse
import com.yumaoem.feature_home.data.dto.beacon_details.request.BeaconDetailsRequest
import com.yumaoem.feature_home.data.dto.book_token.request.BookTokenRequest
import com.yumaoem.feature_home.data.dto.maps_screen.all_stations.request.AllStationsRequest
import com.yumaoem.feature_home.data.dto.maps_screen.all_stations.toDomain
import com.yumaoem.feature_home.data.dto.maps_screen.all_stations.toDomain2
import com.yumaoem.feature_home.data.dto.start_diy_swap.StartDiySwapRequestDto
import com.yumaoem.feature_home.data.dto.start_diy_swap.StartDiySwapResponseDTO
import com.yumaoem.feature_home.data.dto.station_directions.request.RouteInfoRequest
import com.yumaoem.feature_home.data.dto.station_directions.toDomain
import com.yumaoem.feature_home.data.dto.station_operation_status.request.StationOperationStatusRequest
import com.yumaoem.feature_home.data.dto.swap_history.request.SwapHistoryRequest
import com.yumaoem.feature_home.data.dto.swap_history.response.toDomain
import com.yumaoem.feature_home.data.dto.token_status.response.toDomain
import com.yumaoem.feature_home.data.dto.whatsapp_support_details.SupportDetailsRequestDto
import com.yumaoem.feature_home.data.dto.tag_battery.request.TagBatteryRequestDTO
import com.yumaoem.feature_home.data.dto.whatsapp_support_details.toDomain
import com.yumaoem.feature_home.data.dto.check_in_user.location_validation.LocationValidationRequestDTO
import com.yumaoem.feature_home.data.dto.check_in_user.location_validation.LocationValidationResponseDTO
import com.yumaoem.feature_home.data.dto.logout.LogoutResponseDTO
import com.yumaoem.feature_home.data.dto.logout.LogoutUserRequestDTO
import com.yumaoem.feature_home.data.dto.revert_token_checkin.RevertTokenCheckInResponseDTO
import com.yumaoem.feature_home.data.dto.verify_batteries.request.VerifyBatteriesDTO
import com.yumaoem.feature_home.data.network.HomeRemoteDataSource
import com.yumaoem.feature_home.data.network.YuzenRemoteDataSource
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
import com.yumaoem.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow


class HomeRepositoryImpl(
    private val homeRemoteDataSource: HomeRemoteDataSource,
    private val yuzenRemoteDataSource: YuzenRemoteDataSource
) : HomeRepository {

    override suspend fun getBeaconDetails(
        request: BeaconDetailsRequest,
    ): Flow<RestClientResult<List<Beacon>>> = getFlowResult {
        homeRemoteDataSource.getBeaconDetails(request).mapFromDTO { dto ->
            listOf(dto.toDomain())
        }
    }

    override suspend fun bookToken(
        request: BookTokenRequest,
    ): Flow<RestClientResult<BookedTokenDetails>> = getFlowResult {
        homeRemoteDataSource.bookToken(request).mapFromDTO { dto ->
            dto.toDomain()
        }
    }

    override suspend fun getAllStations(
        request: AllStationsRequest,
    ): Flow<RestClientResult<List<YumaStationMarker>>>  = getFlowResult {
        homeRemoteDataSource.getAllStations(request)
            .mapFromDTO { dtoList ->
                dtoList.toDomain()
            }
    }

    override suspend fun getRouteInfo(routeInfoRequest: RouteInfoRequest): Flow<RestClientResult<RouteInfo?>> =
        getFlowResult {
            homeRemoteDataSource.getRouteByRoad(routeInfoRequest).mapFromDTO { dto ->
                dto.toDomain()
            }
        }

    override suspend fun getStationStatus(
        stationStatusRequest: StationOperationStatusRequest,
    ): Flow<RestClientResult<YumaStationStatus>> = getFlowResult {
        homeRemoteDataSource.getStationOperationStatus(stationStatusRequest).mapFromDTO { dto ->
            dto.toDomain2()
        }
    }

    override suspend fun checkInUser(tokenId: Int): Flow<RestClientResult<CheckInResponse>> =
        getFlowResult {
            homeRemoteDataSource.checkInUser(tokenId).mapFromDTO { dto ->
                dto.toDomain()
            }
        }

    override suspend fun cancelTokenBooking(
        tokenId: Int,
    ): Flow<RestClientResult<CancelTokenResponse>> = getFlowResult {
        homeRemoteDataSource.cancelToken(tokenId).mapFromDTO { dto ->
            dto.toDomain()
        }
    }

    override suspend fun getTokenStatus(
        tokenId: Int,
    ): Flow<RestClientResult<TokenStatus>> = getFlowResult {
        homeRemoteDataSource.getTokenStatus(tokenId).mapFromDTO { dto ->
            dto.toDomain()
        }
    }

    override suspend fun getDropOffScreen(
        clientUserId: Int
    ): Flow<RestClientResult<DropOffScreenData>> = getFlowResult {
        homeRemoteDataSource.getDropOffScreen(clientUserId).mapFromDTO { dto->
            dto.toDomain()
        }
    }

    override suspend fun getBatteryDetails(
        clientVehicleId: Int
    ): Flow<RestClientResult<List<BatteryDetails>>> = getFlowResult {
        homeRemoteDataSource.getBatteryDetails(clientVehicleId).mapFromDTO { dto ->
            dto.map {
                it.toDomain()
            }
        }
    }

    override suspend fun getSwapHistory(
        swapHistoryRequest: SwapHistoryRequest
    ): RestClientResult<List<SwapHistoryItem>> {
        return homeRemoteDataSource.getSwapHistory(swapHistoryRequest).mapFromDTO { dto->
            dto.toDomain()
        }
    }

    override suspend fun getSupportDetails(requestDto: SupportDetailsRequestDto): Flow<RestClientResult<SupportDetails>> = getFlowResult {
        homeRemoteDataSource.getSupportDetails(requestDto).mapFromDTO {
            it.toDomain()
        }
    }

    override suspend fun startDiySwap(request: StartDiySwapRequestDto): Flow<RestClientResult<StartDiySwapResponseDTO>> = getFlowResult {
        yuzenRemoteDataSource.startDiySwap(request)
    }

    override suspend fun revertTokenCheckIn(tokenId: Int): Flow<RestClientResult<RevertTokenCheckInResponseDTO>> = getFlowResult {
        homeRemoteDataSource.revertTokenCheckIn(tokenId)
    }

    override suspend fun logoutUser(requestDTO: LogoutUserRequestDTO): Flow<RestClientResult<LogoutResponseDTO>> = getFlowResult {
        homeRemoteDataSource.logoutUser(requestDTO)
    }

    override suspend fun autoDialerRequest(
        requestDTO: AutoDialerRequest
    ): Flow<RestClientResult<AutoDialerResponse>>  = getFlowResult {
        homeRemoteDataSource.requestCustomerSupportCall(requestDTO)
    }

    override suspend fun validateLocation(
        request: LocationValidationRequestDTO
    ): Flow<RestClientResult<LocationValidationResponseDTO>> = getFlowResult {
        homeRemoteDataSource.validateLocation(request)
    }

    override suspend fun mapNewBatteriesOnBike(
        request: TagBatteryRequestDTO
    ): Flow<RestClientResult<GenericSuccessResponseDto>> = getFlowResult {
        yuzenRemoteDataSource.mapNewBatteriesOnBike(request)
    }

    override suspend fun verifyBatteries(
        request: VerifyBatteriesDTO
    ) : Flow<RestClientResult<VerifyBatteries>> = getFlowResult {
        homeRemoteDataSource.verifyBatteryDetails(request).mapFromDTO { data ->
            VerifyBatteries(data = data)
        }
    }

}