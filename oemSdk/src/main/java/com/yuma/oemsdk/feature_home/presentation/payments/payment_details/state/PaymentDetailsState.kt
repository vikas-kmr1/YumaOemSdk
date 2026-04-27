package com.yumaoem.feature_home.presentation.payments.payment_details.state

import com.yumaoem.feature_home.domain.model.payments.UiPlan

data class PaymentDetailsState(
    val planDetails: UiPlan? = UiPlan(
        id = "1",
        title = "Basic",
        range = "",
        validity = "",
        totalAmount = "₹0",
        amountWithoutTax = "₹0",
        taxAmount = "₹0"
    ),
    val orderId:String? = null,
    val isCreatingOrder:Boolean = false,
    val paymentDetailsBottomSheetState: PaymentDetailsBottomSheet = PaymentDetailsBottomSheet.Hidden,
)

sealed class PaymentDetailsBottomSheet {
    data object Hidden : PaymentDetailsBottomSheet()
    data object CouldNotFetchPaymentStatus : PaymentDetailsBottomSheet()
}