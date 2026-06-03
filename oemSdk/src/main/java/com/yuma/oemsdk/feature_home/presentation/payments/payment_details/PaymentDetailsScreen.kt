package com.yumaoem.feature_home.presentation.payments.payment_details

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yuma.oemsdk.R
import com.yuma.oemsdk.YumaSdk
import com.yumaoem.core.utils.noRippleDebounceClickable

import com.yumaoem.core_ui.components.buttons.YumaPrimaryButton
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.color_f5f7fa
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.core_ui.utils.snackbar.SnackbarController
import com.yumaoem.core_ui.utils.snackbar.SnackbarEvent
import com.yumaoem.feature_home.domain.model.payments.UiPlan
import com.yumaoem.feature_home.presentation.payments.payment_details.state.PaymentDetailsBottomSheet


@Composable
fun PaymentDetailsScreenRoot(
    isPaymentsTab: Boolean,
    plan: UiPlan,
    onBackClicked: () -> Unit,
    navigateToPaymentSuccessScreen: () -> Unit,
    navigateToHomeTab: () -> Unit
) {
    val viewModel: PaymentDetailsViewmodel = viewModel(factory = YumaSdk.paymentDetailsViewModelFactory)
    val state = viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setCurrentPlan(plan)
    }

    if (state.value.planDetails!=null && isPaymentsTab){
        PaymentDetailsScreen(
            state = state.value.planDetails!!,
            onBackClicked = onBackClicked,
            onPayClicked = {
                viewModel.createOrder()
            },
            isCreatingOrder = state.value.isCreatingOrder
        )
    }
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is PaymentDetailsUiEvent.PaymentFailed -> {
                    SnackbarController.sendEvent(
                        SnackbarEvent(
                            message = event.message,
                            duration = SnackbarDuration.Short
                        )
                    )
                }
                is PaymentDetailsUiEvent.PaymentSuccess -> {
                    navigateToPaymentSuccessScreen()
                }

                is PaymentDetailsUiEvent.Error -> {
                    SnackbarController.sendEvent(
                        event = SnackbarEvent(
                            message = event.message
                        )
                    )
                }

                is PaymentDetailsUiEvent.NavigateToPaymentHomeScreen -> {
                    navigateToHomeTab()
                }
            }
        }
    }

    when (state.value.paymentDetailsBottomSheetState) {
        PaymentDetailsBottomSheet.CouldNotFetchPaymentStatus -> {
            CouldNotFetchPaymentStatus(
                showSheet = true,
                onBackClick = {
                    navigateToHomeTab()
                }
            )
        }
        PaymentDetailsBottomSheet.Hidden -> {}
    }
}

@Composable
fun PaymentDetailsScreen(
    isCreatingOrder:Boolean = false,
    state: UiPlan,
    onBackClicked: () -> Unit = {},
    onPayClicked: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(top = 28.dp)
            .fillMaxSize()
            .background(color_f5f7fa)
    ) {
        Column {
            PaymentDetailsScreenToolbar(
                modifier = Modifier
                    .height(60.dp)
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = LocalDimensions.current.dimen20dp),
                onBackClicked = onBackClicked
            )
            Spacer(modifier = Modifier.height(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = LocalDimensions.current.dimen20dp)
            ) {
                Text(
                    text = "Plan Details",
                    style = LocalTypography.current.smallBodySemiBold.copy()
                )
                Spacer(modifier = Modifier.height(16.dp))
                PlanCard(
                    swaps = state.title,
                    days = state.validity,
                    price = state.totalAmount
                )
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Choose Payment Method",
                    style = LocalTypography.current.smallBodySemiBold.copy()
                )
                Spacer(modifier = Modifier.height(16.dp))
                PaymentMethodItem(onClick = {})
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = "Bill Details",
                    style = LocalTypography.current.smallBodySemiBold.copy()
                )
                Spacer(modifier = Modifier.height(30.dp))
                BillDetailsCard(
                    planAmount = state.amountWithoutTax,
                    gstAndTax = state.taxAmount,
                    totalAmount = state.totalAmount
                )
            }
        }


        PaymentDetailsScreenFooter(
            totalAmount = state.totalAmount,
            modifier = Modifier
                .align(Alignment.BottomCenter),
            onPayClicked = onPayClicked,
            isLoading = isCreatingOrder
        )
    }
}

@Composable
fun PaymentDetailsScreenFooter(
    isLoading:Boolean,
    totalAmount: String,
    modifier: Modifier = Modifier,
    onPayClicked: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                start = 30.dp, end = 20.dp,
                top = 20.dp, bottom = 20.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Total",
                style = LocalTypography.current.smallBodySemiBold.copy()
            )
            Text(
                text = totalAmount,
                style = LocalTypography.current.bodyLargeSemiBold.copy()
            )
        }
        Spacer(modifier = Modifier.width(40.dp))
        YumaPrimaryButton(
            isLoading = isLoading,
            buttonText = "Pay",
            onClick = onPayClicked,
        )
    }
}

@Composable
fun PaymentDetailsScreenToolbar(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Image(
            painter = painterResource(R.drawable.back_icon),
            contentDescription = "",
            modifier = Modifier
                .padding(end = 20.dp)
                .noRippleDebounceClickable {
                    onBackClicked()
                }
        )
        Text(
            text = "Payment",
            style = LocalTypography.current.bodyLargeSemiBold
        )
    }
}

@Preview
@Composable
fun PaymentDetailsScreenPreview() {
    YumaAppTheme {
        PaymentDetailsScreen(
            state = UiPlan(
                id = "1",
                title = "10 Swaps",
                range = "30 Days",
                validity = "₹499",
                totalAmount = "₹548",
                amountWithoutTax = "₹499",
                taxAmount = "₹49"
            ),
            onPayClicked = {

            }
        )
    }
}