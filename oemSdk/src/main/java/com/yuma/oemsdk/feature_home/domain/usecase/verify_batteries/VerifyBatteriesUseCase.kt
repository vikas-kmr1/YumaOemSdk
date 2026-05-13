package com.yumaoem.feature_home.domain.usecase.verify_batteries

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.data.dto.verify_batteries.request.VerifyBatteriesDTO
import com.yumaoem.feature_home.domain.model.token_flow.verify_batteries.VerifyBatteries
import com.yumaoem.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class VerifyBatteriesUseCase (
    private val repository: HomeRepository
) {
    suspend operator fun invoke(
        request: VerifyBatteriesDTO
    ) : Flow<RestClientResult<VerifyBatteries>> = repository.verifyBatteries(request)
}