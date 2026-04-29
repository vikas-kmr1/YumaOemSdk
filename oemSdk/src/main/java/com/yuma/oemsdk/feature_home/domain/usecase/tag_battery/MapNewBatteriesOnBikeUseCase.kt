package com.yumaoem.feature_home.domain.usecase.tag_battery

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.data.dto.GenericSuccessResponseDto
import com.yumaoem.feature_home.data.dto.tag_battery.request.TagBatteryRequestDTO
import com.yumaoem.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class MapNewBatteriesOnBikeUseCase(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(
        request: TagBatteryRequestDTO
    ): Flow<RestClientResult<GenericSuccessResponseDto>> = repository.mapNewBatteriesOnBike(request)
}
