package com.yumaoem.feature_home.domain.usecase.support_details

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.data.dto.whatsapp_support_details.SupportDetailsRequestDto
import com.yumaoem.feature_home.domain.model.supportDetails.SupportDetails
import com.yumaoem.feature_home.domain.repository.HomeRepository
import kotlinx.coroutines.flow.Flow

class GetWhatsappSupprtDetailsUseCase (
    private val repository: HomeRepository
) {
    suspend operator fun invoke(
        requestDto: SupportDetailsRequestDto
    ): Flow<RestClientResult<SupportDetails>> = repository.getSupportDetails(requestDto)
}