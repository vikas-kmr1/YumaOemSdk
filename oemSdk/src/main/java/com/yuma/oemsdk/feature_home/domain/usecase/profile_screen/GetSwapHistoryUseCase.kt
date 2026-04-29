package com.yumaoem.feature_home.domain.usecase.profile_screen

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.data.dto.swap_history.request.SwapHistoryRequest
import com.yumaoem.feature_home.domain.model.profile.swap_history.SwapHistoryItem
import com.yumaoem.feature_home.domain.repository.HomeRepository

class GetSwapHistoryUseCase(
    private val repository: HomeRepository
) {
    suspend fun invoke(
        swapHistoryRequest: SwapHistoryRequest
    ): RestClientResult<List<SwapHistoryItem>> = repository.getSwapHistory(swapHistoryRequest)
}
