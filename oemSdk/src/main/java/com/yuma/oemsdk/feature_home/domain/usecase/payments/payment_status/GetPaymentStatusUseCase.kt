package com.yumaoem.feature_home.domain.usecase.payments.payment_status

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.data.dto.payment_plans.payment_status.PaymentStatusRequestDTO
import com.yumaoem.feature_home.domain.model.payments.PaymentStatus
import com.yumaoem.feature_home.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow

class GetPaymentStatusUseCase (
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(request: PaymentStatusRequestDTO): Flow<RestClientResult<PaymentStatus>> = repository.getPaymentStatus(request)
}