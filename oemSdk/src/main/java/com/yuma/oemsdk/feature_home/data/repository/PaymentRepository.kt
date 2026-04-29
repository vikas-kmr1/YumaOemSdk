package com.yumaoem.feature_home.data.repository

import com.yumaoem.core_network.impl.data.model.RestClientResult
import com.yumaoem.core_network.impl.util.getFlowResult
import com.yumaoem.core_network.impl.util.mapFromDTO
import com.yumaoem.feature_home.data.dto.payment_plans.create_order.CreateOrderRequestDTO
import com.yumaoem.feature_home.data.dto.payment_plans.payment_status.PaymentStatusRequestDTO
import com.yumaoem.feature_home.data.dto.payment_plans.toUiModel
import com.yumaoem.feature_home.data.network.HomeRemoteDataSource
import com.yumaoem.feature_home.domain.model.payments.CreateOrder
import com.yumaoem.feature_home.domain.model.payments.PaymentPlansUiModel
import com.yumaoem.feature_home.domain.model.payments.PaymentStatus
import com.yumaoem.feature_home.domain.repository.PaymentRepository
import kotlinx.coroutines.flow.Flow

class PaymentRepositoryImpl(
    private val homeRemoteDataSource: HomeRemoteDataSource
) : PaymentRepository {
    override suspend fun getPaymentPlans(clientVehicleId: String):
            Flow<RestClientResult<PaymentPlansUiModel>> = getFlowResult {
        homeRemoteDataSource.getPaymentPlans(clientVehicleId).mapFromDTO { dto ->
            dto.toUiModel()
        }
    }

    override suspend fun getPaymentStatus(
        requestDTO: PaymentStatusRequestDTO
    ): Flow<RestClientResult<PaymentStatus>> = getFlowResult {
        homeRemoteDataSource.getPaymentStatus(requestDTO).mapFromDTO { dto ->
            dto.toDomain()
        }
    }

    override suspend fun createOrder(
        requestDTO: CreateOrderRequestDTO
    ): Flow<RestClientResult<CreateOrder>> = getFlowResult {
        homeRemoteDataSource.createOrder(requestDTO).mapFromDTO { dto ->
            dto.toDomain()
        }
    }
}