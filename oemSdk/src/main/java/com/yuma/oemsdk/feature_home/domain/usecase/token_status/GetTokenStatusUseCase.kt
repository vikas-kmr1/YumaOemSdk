package com.yumaoem.feature_home.domain.usecase.token_status

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.domain.model.token_flow.beacon_details.Beacon
import com.yumaoem.feature_home.domain.model.token_flow.token_status.TokenStatus
import com.yumaoem.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class GetTokenStatusUseCase (
    private val repository: HomeRepository
) {
    suspend operator fun invoke(
        tokenId:Int
    ): Flow<RestClientResult<TokenStatus>> = repository.getTokenStatus(tokenId)
}