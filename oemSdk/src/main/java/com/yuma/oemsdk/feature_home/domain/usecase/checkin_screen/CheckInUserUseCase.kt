package com.yumaoem.feature_home.domain.usecase.checkin_screen

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.domain.model.token_flow.check_in.CheckInResponse
import com.yumaoem.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class CheckInUserUseCase (
    private val repository: HomeRepository
) {
    suspend operator fun invoke(
        tokenId:Int
    ): Flow<RestClientResult<CheckInResponse>> = repository.checkInUser(tokenId)
}