package com.yumaoem.feature_home.domain.usecase.beacon_details

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.data.dto.beacon_details.request.BeaconDetailsRequest
import com.yumaoem.feature_home.domain.model.token_flow.beacon_details.Beacon
import com.yumaoem.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class GetBeaconDetailsUseCase (
    private val repository: HomeRepository
) {
    suspend operator fun invoke(
        request: BeaconDetailsRequest
    ): Flow<RestClientResult<List<Beacon>>> = repository.getBeaconDetails(request)
}