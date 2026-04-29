package com.yumaoem.feature_onboarding.domain.use_case.drop_off

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.domain.model.drop_off_data.DropOffScreenData
import com.yumaoem.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class GetDropOffDataUseCase (
    private val repository: HomeRepository
) {
    suspend operator fun invoke(
        clientUserId:Int
    ): Flow<RestClientResult<DropOffScreenData>> = repository.getDropOffScreen(clientUserId)
}