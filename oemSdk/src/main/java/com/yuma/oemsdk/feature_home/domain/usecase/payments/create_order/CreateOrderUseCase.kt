package com.yumaoem.feature_home.domain.usecase.payments.create_order

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.data.dto.payment_plans.create_order.CreateOrderRequestDTO
import com.yumaoem.feature_home.data.dto.payment_plans.payment_status.PaymentStatusRequestDTO
import com.yumaoem.feature_home.domain.model.payments.CreateOrder
import com.yumaoem.feature_home.domain.model.payments.PaymentStatus
import com.yumaoem.feature_home.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow

class CreateOrderUseCase (
    private val repository: PaymentRepository
) {
    suspend operator fun invoke(request: CreateOrderRequestDTO): Flow<RestClientResult<CreateOrder>> = repository.createOrder(request)
}