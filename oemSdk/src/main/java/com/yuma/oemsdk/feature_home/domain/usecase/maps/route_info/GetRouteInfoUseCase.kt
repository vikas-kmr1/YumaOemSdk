package com.yumaoem.feature_home.domain.usecase.maps.route_info

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.data.dto.station_directions.request.RouteInfoRequest
import com.yumaoem.feature_home.domain.model.maps.stationRoute.RouteInfo
import com.yumaoem.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class GetRouteInfoUseCase (
    private val repository: HomeRepository
) {
    suspend operator fun invoke(
        routeInfoRequest: RouteInfoRequest
    ): Flow<RestClientResult<RouteInfo?>> = repository.getRouteInfo(routeInfoRequest)
}