package com.yumaoem.feature_home.domain.usecase.maps.station_operation_status

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.data.dto.station_operation_status.request.StationOperationStatusRequest
import com.yumaoem.feature_home.domain.model.maps.all_station_markers.YumaStationStatus
import com.yumaoem.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class GetStationOperationStatusUseCase (
    private val repository: HomeRepository
) {
    suspend operator fun invoke(
        stationOperationStatusRequest: StationOperationStatusRequest
    ): Flow<RestClientResult<YumaStationStatus>> = repository.getStationStatus(stationOperationStatusRequest)
}