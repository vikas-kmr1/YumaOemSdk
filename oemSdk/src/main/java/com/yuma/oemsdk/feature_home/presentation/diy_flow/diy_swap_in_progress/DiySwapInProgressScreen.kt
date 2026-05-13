package com.yuma.oemsdk.feature_home.presentation.diy_flow.diy_swap_in_progress

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yuma.oemsdk.R
import com.yuma.oemsdk.YumaSdk
import com.yumaoem.core.utils.noRippleDebounceClickable
import com.yumaoem.core_ui.components.buttons.YumaPrimaryButton
import com.yumaoem.core_ui.components.snackbar.SuccessSnackbar
import com.yumaoem.core_ui.components.step_indicator.StepIndicator
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.core_ui.utils.snackbar.SnackbarController
import com.yumaoem.core_ui.utils.snackbar.SnackbarEvent
import com.yumaoem.feature_home.presentation.diy_flow.dialogs.MultiYcuInfoDialog
import com.yumaoem.feature_home.presentation.diy_flow.dialogs.SwapInfoDialog
import com.yumaoem.feature_home.presentation.diy_flow.diy_scan_battery_screen.DiyScanBatteryScreen
import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.DiySwapBottomSheet
import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.DiySwapDialog
import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.DiySwapInProgressEvent
import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.DiySwapInProgressState
import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.DiySwapInProgressUiEvent
import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.DiySwapInProgressViewModel
import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.args.SwapInProgressScreenArgs
import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.components.GetCallbackContentModalBottomSheet
import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.diy_multi_ycu_swap_in_progress.DiyMultiYcuSwapInProgressScreen
import com.yumaoem.feature_home.presentation.diy_flow.error_bottom_sheets.BluetoothConnectionFailedModalBottomSheet
import com.yumaoem.feature_home.presentation.diy_flow.error_bottom_sheets.CBOpenFailedModalBottomSheet
import com.yumaoem.feature_home.presentation.diy_flow.error_bottom_sheets.DBDoNotInsertBatteryModalSheet
import com.yumaoem.feature_home.presentation.diy_flow.error_bottom_sheets.DBInsertFailedCustomerSupportModalBottomSheet
import com.yumaoem.feature_home.presentation.diy_flow.error_bottom_sheets.DBInsertFailedModalBottomSheet
import com.yumaoem.feature_home.presentation.diy_flow.error_bottom_sheets.RemoteSwapInProgressModalBottomSheet
import com.yumaoem.feature_home.presentation.diy_flow.error_bottom_sheets.ScanAnotherMachineModalBottomSheet
import com.yumaoem.feature_home.presentation.diy_flow.error_bottom_sheets.ScanBatteryTryAgainModalBottomSheet
import com.yumaoem.feature_home.presentation.diy_flow.error_bottom_sheets.SomethingWentWrongModalBottomSheet
import com.yumaoem.feature_home.presentation.diy_flow.error_bottom_sheets.SwapStatusNotCompletedModalBottomSheet
import com.yumaoem.feature_home.presentation.profile_screen.components.BatteryQrIcon
import com.yumaoem.feature_home.presentation.profile_screen.components.BikeDetailsBottomSheet
import kotlinx.coroutines.launch


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DiySwapInProgressScreenRoot(
    args: SwapInProgressScreenArgs,
    onRetry: () -> Unit,
    onSuccessfulSwap: (String) -> Unit,
    onPartialSwapSuccess: () -> Unit,
    currentBatterySwap: Int = 1,
    isMultiYcuSwap: Boolean = false,
    isHomeTab: Boolean
) {
    val viewModel: DiySwapInProgressViewModel =
        viewModel(factory = YumaSdk.diySwapInProgressViewModelFactory)
    val state by viewModel.state.collectAsState()
    val isMultiBatteryFlow: State<Boolean> = remember {
        derivedStateOf {
            !isMultiYcuSwap && state.batteryCount > 1
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (isMultiYcuSwap) {
            viewModel.setMultiYcuArgs(currentBatterySwap, isMultiYcuSwap)
        }
        viewModel.setSwapInProgressArgs(args)
    }

    BackHandler(enabled = state.showScanBatteryScreen) {
        viewModel.onEvent(DiySwapInProgressEvent.OnBackClicked)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is DiySwapInProgressUiEvent.SwapCompleted -> {
                    viewModel.sendSwapCompleteEvent()
                    onSuccessfulSwap(event.swapTime)
                }

                is DiySwapInProgressUiEvent.ShowSuccessSnackbar -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is DiySwapInProgressUiEvent.ShowError -> {
                    coroutineScope.launch {
                        SnackbarController.sendEvent(
                            event = SnackbarEvent(message = event.message)
                        )
                    }
                }
            }
        }
    }


    DiySwapBottomSheetHost(
        bottomSheet = state.bottomSheet,
        onRetry = onRetry,
        onDismiss = {
            viewModel.dismissBottomSheet()
        },
        onCustomerSupportClicked = {
            viewModel.showCustomerSupportBottomSheet()
        },
        onReceiveCallClicked = {
            viewModel.requestCall(it)
            viewModel.sendCsReceiveCallEvent(it)
        },
        onRetryBatteryScan = {
            viewModel.onEvent(DiySwapInProgressEvent.RetryBatteryScan)
        },
        viewModel = viewModel
    )

    DiySwapDialogHost(
        dialog = state.dialog,
        onContinue = {
            viewModel.onEvent(DiySwapInProgressEvent.OnContinueClicked)
        },
        onScanQr = {
            viewModel.sendMultiYcuCtaBtnClickedEvent(
                actionName = "pop_up_scan_qr_cta",
                isSubmitButton = false,
                status = false,
                failureReason = ""
            )
            onPartialSwapSuccess()
        }
    )

    if (isHomeTab) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data ->
                    SuccessSnackbar(data = data)
                }
            }
        ) {innerPadding ->
            if (state.showScanBatteryScreen.not()) {
                if (state.isMultiYcuSwap.not()) {
                    LaunchedEffect(Unit) {
                        viewModel.sendDiySwapInProgressScreenViewedEvent()
                    }
                    DiySwapInProgressScreen(
                        state = state,
                        toggleBottomSheet = {
                            viewModel.onEvent(DiySwapInProgressEvent.ToggleBikeDetailsBottomSheet)
                            viewModel.sendBottomSheetClickedEvent()
                        },
                        isMultiYcuSwap = state.isMultiYcuSwap,
                        onSubmitButtonClicked = {
                            viewModel.onSubmitClicked()
                        },
                        onCustomerSupportClicked = {
                            viewModel.showCustomerSupportBottomSheet()
                            viewModel.sendCsButtonClickedEvent()
                        }
                    )
                } else {
                    DiyMultiYcuSwapInProgressScreen(
                        state = state,
                        isSubmitButtonVisible = state.isSubmitButtonVisible,
                        isSubmitting = state.isSubmitting,
                        onSubmitButtonClicked = {
                            viewModel.onSubmitClicked()
                        },
                        onCustomerSupportClicked = {
                            viewModel.showCustomerSupportBottomSheet()
                        },
                        toggleBottomSheet = {
                            viewModel.onEvent(event = DiySwapInProgressEvent.ToggleBikeDetailsBottomSheet)
                        }
                    )
                }
            } else {
                LaunchedEffect(Unit) {
                    viewModel.sendManualBatteryScreenViewedEvent()
                }
                DiyScanBatteryScreen(
                    onBackClicked = {
                        viewModel.onEvent(DiySwapInProgressEvent.OnBackClicked)
                    },
                    onFlashLightClicked = {
                        viewModel.onEvent(DiySwapInProgressEvent.ToggleFlashlight)
                    },
                    onScanCompleted = { result, isManualEntry ->
                        viewModel.onEvent(
                            DiySwapInProgressEvent.OnBatteryScanned(
                                result,
                                isManualEntry
                            )
                        )
                    },
                    onCustomerSupportClicked = {
                        viewModel.showCustomerSupportBottomSheet()
                    },
                    isLoading = state.isSubmitting,
                    isMultiBatteryFlow = isMultiBatteryFlow.value,
                    scannedBatteryCount = state.batteryQrList.size,
                    totalBatteryCount = state.batteryCount,
                    isFlashLightOn = state.isFlashlightOn,
                    showCustomerSupport = true,
                    isMultiYcuSwap = state.isMultiYcuSwap
                )
            }
        }
    }

}


@Composable
private fun DiySwapInProgressScreen(
    state: DiySwapInProgressState,
    toggleBottomSheet: () -> Unit,
    isMultiYcuSwap: Boolean,
    onSubmitButtonClicked: () -> Unit,
    onCustomerSupportClicked: () -> Unit,
) {

    if (state.showBikeDetailsBottomSheet) {
        BikeDetailsBottomSheet(
            bikeProvider = state.bikeDetails.bikeProvider,
            bikeNumber = state.bikeDetails.bikeNumber,
            qrNumber = state.bikeDetails.bikeQrNumber,
            batteryIds = state.bikeDetails.batteryDetails,
            onDismissRequest = toggleBottomSheet
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                strokeWidth = 4.dp,
                modifier = Modifier.size(48.dp),

                )
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                textAlign = TextAlign.Center,
                text = "Follow steps on the YCU screen\n to complete swap",
                style = LocalTypography.current.bodyLargeSemiBold.copy(
                    color = LocalColors.current.neutral[Colors.TYPE_500.ordinal]
                )
            )
        }

        DiySwapInProgressScreenStationHeader(
            stationName = state.stationName,
            stationId = state.stationId,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            toggleBottomSheet = toggleBottomSheet,
            isMultiYcuSwap = isMultiYcuSwap
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Bottom
        ) {
            if (state.isSubmitButtonVisible) {
                YumaPrimaryButton(
                    buttonText = "Submit",
                    onClick = onSubmitButtonClicked,
                    isLoading = state.isSubmitting,
                    modifier = Modifier
                        .padding(horizontal = LocalDimensions.current.dimen20dp)
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            DiySwapNeedHelpFooter(
                modifier = Modifier.fillMaxWidth(),
                onCustomerSupportClicked = onCustomerSupportClicked
            )
        }
    }
}

@Composable
fun DiySwapInProgressScreenStationHeader(
    stationName: String,
    stationId: String,
    modifier: Modifier = Modifier,
    toggleBottomSheet: () -> Unit,
    hideStepIndicator: Boolean = false,
    isMultiYcuSwap: Boolean = false
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = Color(0xFFF5F7FA)
                )
                .padding(start = 24.dp, end = 24.dp, bottom = 18.dp, top = 50.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Station Name",
                    style = LocalTypography.current.smallBodyMedium.copy(
                        textAlign = TextAlign.Start
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$stationName - $stationId",
                    style = LocalTypography.current.smallBodySemiBold
                )
            }
            BatteryQrIcon(
                toggleBottomSheet = toggleBottomSheet
            )
        }
        if(!hideStepIndicator) {
            Spacer(modifier = Modifier.height(30.dp))
            StepIndicator(
                totalSteps = 3,
                currentStep = 3,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp)
            )
        }
        if(!isMultiYcuSwap) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                textAlign = TextAlign.Center,
                text = "Swap in progress",
                style = LocalTypography.current.bodyLargeSemiBold.copy(
                    color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
                ),
            )
        }
    }
}


@Composable
fun DiySwapNeedHelpFooter(
    modifier: Modifier = Modifier,
    onCustomerSupportClicked: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF5F7FA)
            )
            .padding(horizontal = 20.dp, vertical = 40.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Need help with the swap?",
            style = LocalTypography.current.bodyMedium.copy(
                color = Color(0xFF717171)
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(horizontal = 42.dp)
                .noRippleDebounceClickable(
                    onClick = onCustomerSupportClicked
                )
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
                    shape = RoundedCornerShape(38.dp)
                )
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Image(
                painter = painterResource(R.drawable.ic_phone_outlined),
                contentDescription = "phone icon"
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Customer Support",
                style = LocalTypography.current.bodyMedium.copy(
                    color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiySwapBottomSheetHost(
    onDismiss: () -> Unit,
    onCustomerSupportClicked: () -> Unit,
    onReceiveCallClicked: (String) -> Unit,
    bottomSheet: DiySwapBottomSheet,
    onRetry: () -> Unit,
    onRetryBatteryScan: () -> Unit,
    viewModel: DiySwapInProgressViewModel
) {
    if (bottomSheet != DiySwapBottomSheet.None) {
        when (bottomSheet) {
            is DiySwapBottomSheet.SomethingWentWrong -> {
                SomethingWentWrongModalBottomSheet(
                    showSheet = true,
                    onDismiss = {},
                    onRetry = onRetry
                )
            }

            is DiySwapBottomSheet.BluetoothConnectionFailed -> {
                BluetoothConnectionFailedModalBottomSheet(
                    showSheet = true,
                    onDismiss = {},
                    onRetry = onRetry
                )
            }

            DiySwapBottomSheet.None -> {}

            is DiySwapBottomSheet.GetCallbackBottomSheet -> {
                GetCallbackContentModalBottomSheet(
                    mobileNumber = bottomSheet.mobileNumber,
                    isLoading = bottomSheet.isLoading,
                    onDismiss = onDismiss,
                    onReceiveCallClicked = onReceiveCallClicked
                )
            }

            DiySwapBottomSheet.NoChargedBatteryFound -> {
                ScanAnotherMachineModalBottomSheet(
                    showSheet = true,
                    onDismiss = onDismiss,
                    onRetry = onRetry
                )
            }

            DiySwapBottomSheet.SubmitFailureScanAgain -> {
                ScanBatteryTryAgainModalBottomSheet(
                    onRetry = onRetryBatteryScan
                )
            }

            DiySwapBottomSheet.RemoteSwapInProgress -> {
                RemoteSwapInProgressModalBottomSheet(
                    onRetry = onDismiss,
                    onDismiss = onDismiss
                )
            }

            DiySwapBottomSheet.SwapStatusNotCompleted -> {
                SwapStatusNotCompletedModalBottomSheet(
                    onRetry = {
                        onDismiss()
                    },
                    onDismiss = onDismiss
                )
            }

            DiySwapBottomSheet.CBOpenFailed -> {
                CBOpenFailedModalBottomSheet(
                    onCustomerSupportClicked = {
                        onCustomerSupportClicked()
                    },
                    onDismiss = onDismiss
                )
            }

            DiySwapBottomSheet.DBInsertFailed -> {
                DBInsertFailedModalBottomSheet(
                    onRetry = {
                        onDismiss()
                    },
                    onDismiss = onDismiss
                )
            }

            DiySwapBottomSheet.DBInsertFailedCustomerSupport -> {
                DBInsertFailedCustomerSupportModalBottomSheet(
                    onScanMachine = {
                        viewModel.cleanupSession()
                        onRetry()
                    },
                    onCustomerSupportClicked = {
                        onCustomerSupportClicked()
                    },
                    onDismiss = onDismiss
                )
            }

            DiySwapBottomSheet.DBDoNotInsertBatteryModalSheet -> {
                DBDoNotInsertBatteryModalSheet(
                    onCustomerSupportClicked = {
                        onCustomerSupportClicked()
                    },
                    onDismiss = onDismiss
                )
            }
        }
    }
}

@Composable
fun DiySwapDialogHost(
    dialog: DiySwapDialog,
    onContinue: () -> Unit,
    onScanQr: () -> Unit
) {
    if (dialog != DiySwapDialog.None) {
        when (dialog) {
            DiySwapDialog.MultiYcuSwapDialog -> {
                MultiYcuInfoDialog(
                    title = "2 Machine Swap",
                    onAction = onContinue
                )
            }

            DiySwapDialog.SwapInfoDialog -> {
                SwapInfoDialog(
                    title = "Battery 2",
                    onAction = onScanQr
                )
            }

            DiySwapDialog.None -> {}
        }
    }
}

@Preview
@Composable
fun DiySwapInProgressScreenStationHeaderPreview() {
    YumaAppTheme {
        DiySwapInProgressScreenStationHeader(
            stationName = "Yuma Charging",
            stationId = "101",
            toggleBottomSheet = {}
        )
    }
}

@Preview
@Composable
fun DiySwapNeedHelpFooterPreview() {
    YumaAppTheme {
        DiySwapNeedHelpFooter {}
    }
}

@Preview
@Composable
fun DiySwapInProgressScreenPreview() {
    YumaAppTheme {
        DiySwapInProgressScreen(
            state = DiySwapInProgressState(),
            toggleBottomSheet = {},
            isMultiYcuSwap = false,
            onSubmitButtonClicked = {},
            onCustomerSupportClicked = {}
        )
    }
}