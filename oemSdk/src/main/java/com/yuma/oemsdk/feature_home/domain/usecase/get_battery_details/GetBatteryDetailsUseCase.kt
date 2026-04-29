package com.yumaoem.feature_home.domain.usecase.get_battery_details

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.domain.model.token_flow.battery_details.BatteryDetails
import com.yumaoem.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class GetBatteryDetailsUseCase(
    private val repository: HomeRepository
) {
    suspend operator fun invoke(
        clientVehicleId: Int
    ): Flow<RestClientResult<List<BatteryDetails>>> = repository.getBatteryDetails(clientVehicleId)
}