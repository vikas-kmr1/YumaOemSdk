package com.yumaoem.feature_home.data.dto.payment_plans.payment_status

import com.yumaoem.feature_home.domain.model.payments.PaymentStatus
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PaymentStatusResponseDTO(
    @SerialName("order_id")
    val orderId:String? = null,

    @SerialName("payment_status")
    val paymentStatus:String,
) {
    fun toDomain(): PaymentStatus = PaymentStatus(
        orderId = orderId,
        paymentStatus = paymentStatus
    )
}
