package com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.swap_in_progress_screen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yuma.oemsdk.R
import com.yuma.oemsdk.YumaSdk

import com.yumaoem.core_ui.components.buttons.YumaSecondaryButton
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.core_ui.utils.collectAsLaunchedEffect
import com.yumaoem.core_ui.utils.snackbar.SnackbarController
import com.yumaoem.core_ui.utils.snackbar.SnackbarEvent
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.components.TokenNumberViewWithGlow
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.components.TrailingIcon
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.dialogs.CancelBookingConfirmationModalBottomSheet
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.swap_in_progress_screen.TokenQRScreenUiEvent
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.swap_in_progress_screen.TokenQrScreenEvent
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.swap_in_progress_screen.TokenQrScreenViewModel
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.swap_in_progress_screen.TokenStatus


@Composable
fun TokenQrScreenRoot(
    onBookingCancelled: () -> Unit,
    onSwapCompleted: (String) -> Unit,
    isHomeTab: Boolean
) {
    val viewModel: TokenQrScreenViewModel = viewModel(factory = YumaSdk.tokenQrScreenViewModelFactory)
    val state = viewModel.state.value

    viewModel.uiEvent.collectAsLaunchedEffect(Unit) { event ->
        when (event) {
            is TokenQRScreenUiEvent.ShowSnackbar -> {
                SnackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = event.message,
                    )
                )
            }

            TokenQRScreenUiEvent.OnBookingCancelled -> { onBookingCancelled() }

            is TokenQRScreenUiEvent.TokenStatusUpdated -> {
                if (event.tokenStatus == TokenStatus.SWAP_COMPLETED) {
                    onSwapCompleted(event.swapTime)
                }
            }
        }
    }

    if (isHomeTab){
        TokenQrScreen(
            tokenNumber = state.tokenNumber.toString(),
            isCancelBookingDialogVisible = state.cancelBookingDialogVisible,
            isSwapInProgress = state.isSwapInProgress,
            onCancelBookingClicked = {
                viewModel.onEvent(TokenQrScreenEvent.CancelBookingClicked)
            },
            onDialogDismissRequest = {
                viewModel.onEvent(TokenQrScreenEvent.DismissDialog)
            },
            onCancelBookingConfirmed = {
                viewModel.onEvent(TokenQrScreenEvent.CancelBookingConfirmed)
            }
        )
    }
}

@Composable
fun TokenQrScreen(
    tokenNumber: String,
    isSwapInProgress: Boolean = false,
    isCancelBookingDialogVisible: Boolean = false,
    onCancelBookingClicked: () -> Unit,
    onDialogDismissRequest: () -> Unit,
    onCancelBookingConfirmed: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (isCancelBookingDialogVisible) {
            CancelBookingConfirmationModalBottomSheet(
                onDismissRequest = onDialogDismissRequest,
                onCancelBookingConfirmed = onCancelBookingConfirmed
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
            ) {
                TokenNumberViewWithGlow(
                    tokenNumber = tokenNumber,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
            Spacer(Modifier.height(50.dp))
            if (isSwapInProgress){
                Text(
                    text ="Swap in progress",
                    color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
                    style = LocalTypography.current.bodyMedium
                )
            }
        }

        if (!isSwapInProgress){
            CancelBookingView(
                modifier = Modifier
                    .wrapContentHeight()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp, vertical = 40.dp),
                onCancelBookingClicked = onCancelBookingClicked
            )
        }
    }
}



@Composable
fun CancelBookingView(
    modifier: Modifier = Modifier,
    onCancelBookingClicked: () -> Unit,
) {
    YumaSecondaryButton(
        modifier = modifier,
        buttonText = stringResource(R.string.cancel_booking),
        onClick = onCancelBookingClicked,
        trailingIcon = {
            TrailingIcon(onClick = onCancelBookingClicked)
        }
    )
}

@Preview
@Composable
fun TokenQrScreenPreview() {
    YumaAppTheme {
        TokenQrScreen(
            tokenNumber = "45",
            isSwapInProgress = false,
            isCancelBookingDialogVisible = false,
            onCancelBookingClicked = {},
            onDialogDismissRequest = {},
            onCancelBookingConfirmed = {}
        )
    }
}