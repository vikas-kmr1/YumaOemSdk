package com.yumaoem.feature_home.domain.usecase.diy_flow

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.data.dto.GenericSuccessResponseDto
import com.yumaoem.feature_home.data.dto.start_diy_swap.StartDiySwapRequestDto
import com.yumaoem.feature_home.data.dto.start_diy_swap.StartDiySwapResponseDTO
import com.yumaoem.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class StartDiyFlowUseCase (
    private val repository: HomeRepository
) {
    suspend operator fun invoke(
        requestDto: StartDiySwapRequestDto
    ): Flow<RestClientResult<StartDiySwapResponseDTO>> = repository.startDiySwap(requestDto)
}