package com.yumaoem.feature_home.domain.usecase.maps.all_station_markers

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.data.dto.maps_screen.all_stations.request.AllStationsRequest
import com.yumaoem.feature_home.domain.model.maps.all_station_markers.YumaStationMarker
import com.yumaoem.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class GetAllStationsUseCase (
    private val repository: HomeRepository
) {
    suspend operator fun invoke(
        nearbyStationsRequest: AllStationsRequest
    ): Flow<RestClientResult<List<YumaStationMarker>>> = repository.getAllStations(nearbyStationsRequest)
}