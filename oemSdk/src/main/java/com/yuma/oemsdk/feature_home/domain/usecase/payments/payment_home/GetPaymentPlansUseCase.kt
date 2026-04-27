package com.yumaoem.feature_home.domain.usecase.payments.payment_home

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.domain.model.payments.PaymentPlansUiModel
import com.yumaoem.feature_home.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow

class GetPaymentPlansUseCase (
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(clientVehicleId:String): Flow<RestClientResult<PaymentPlansUiModel>> = repository.getPaymentPlans(clientVehicleId)
}