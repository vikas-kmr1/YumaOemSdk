package com.yumaoem.feature_home.domain.repository

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.feature_home.data.dto.payment_plans.create_order.CreateOrderRequestDTO
import com.yumaoem.feature_home.data.dto.payment_plans.payment_status.PaymentStatusRequestDTO
import com.yumaoem.feature_home.domain.model.payments.CreateOrder
import com.yumaoem.feature_home.domain.model.payments.PaymentPlansUiModel
import com.yumaoem.feature_home.domain.model.payments.PaymentStatus
import kotlinx.coroutines.flow.Flow

interface PaymentRepository {
    suspend fun getPaymentPlans(clientVehicleId: String): Flow<RestClientResult<PaymentPlansUiModel>>

    suspend fun getPaymentStatus(requestDTO: PaymentStatusRequestDTO): Flow<RestClientResult<PaymentStatus>>

    suspend fun createOrder(requestDTO: CreateOrderRequestDTO): Flow<RestClientResult<CreateOrder>>
}