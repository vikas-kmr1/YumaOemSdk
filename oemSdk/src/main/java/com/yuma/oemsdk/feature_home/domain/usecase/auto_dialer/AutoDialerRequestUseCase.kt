package com.yumaoem.feature_home.domain.usecase.auto_dialer

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.data.dto.auto_dialer.AutoDialerRequest
import com.yumaoem.feature_home.data.dto.auto_dialer.AutoDialerResponse
import com.yumaoem.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class AutoDialerRequestUseCase (
    private val repository: HomeRepository
) {
    suspend operator fun invoke(
        requestDTO: AutoDialerRequest
    ): Flow<RestClientResult<AutoDialerResponse>> = repository.autoDialerRequest(requestDTO)
}