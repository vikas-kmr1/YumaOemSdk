package com.yumaoem.feature_home.domain.usecase.revert_token_status

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.data.dto.revert_token_checkin.RevertTokenCheckInResponseDTO
import com.yumaoem.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class RevertTokenCheckInStatusUseCase (
    private val repository: HomeRepository
) {
    suspend operator fun invoke(
        tokenId:Int
    ): Flow<RestClientResult<RevertTokenCheckInResponseDTO>> = repository.revertTokenCheckIn(tokenId)
}