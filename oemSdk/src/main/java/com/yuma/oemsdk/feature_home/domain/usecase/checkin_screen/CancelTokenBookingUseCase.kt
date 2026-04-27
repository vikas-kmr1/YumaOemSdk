package com.yumaoem.feature_home.domain.usecase.checkin_screen

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.domain.model.token_flow.cancel_token_booking.CancelTokenResponse
import com.yumaoem.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class CancelTokenBookingUseCase (
    private val repository: HomeRepository
) {
    suspend operator fun invoke(
        tokenId:Int
    ): Flow<RestClientResult<CancelTokenResponse>> = repository.cancelTokenBooking(tokenId)
}