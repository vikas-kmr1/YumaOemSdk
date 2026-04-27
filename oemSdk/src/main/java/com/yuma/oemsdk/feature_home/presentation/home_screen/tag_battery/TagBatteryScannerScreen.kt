package com.yumaoem.feature_home.presentation.home_screen.tag_battery

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi

import com.yumaoem.core_ui.components.snackbar.SuccessSnackbar
import com.yumaoem.core_ui.utils.snackbar.SnackbarController
import com.yumaoem.core_ui.utils.snackbar.SnackbarEvent
import com.yumaoem.feature_home.presentation.diy_flow.diy_scan_battery_screen.DiyScanBatteryScreen
import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.components.GetCallbackContentModalBottomSheet
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TagBatteryScannerScreenRoot(
    isHomeTab: Boolean,
    navigateToMapScreen: () -> Unit,
) {

    //val viewModel = koinViewModel<TagBatteryViewModel>()
    //val state = viewModel.state.collectAsState()
/*
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    BackHandler { }(enabled = true) {
        navigateToMapScreen()
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(DiyScanBatteryIntent.OnScreenViewed)
        viewModel.uiEvent.collect { event ->
            when (event) {
                is TagBatteryUiEvent.ShowError -> {
                    SnackbarController.sendEvent(
                        event = SnackbarEvent(
                            message = event.message,
                        )
                    )
                }

                TagBatteryUiEvent.NavigateToMapScreen -> {
                    navigateToMapScreen()
                }

                is TagBatteryUiEvent.ShowSuccessSnackbar -> {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            duration = SnackbarDuration.Short
                        )
                    }
                }

            }
        }
    }

    TagBatteryBottomSheetHost(
        onDismiss = {
            viewModel.dismissBottomSheet()
        },
        onReceiveCallClicked = {
            viewModel.requestCall(it)
        },
        bottomSheet = state.value.bottomSheet
    )


    if (isHomeTab) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data ->
                    SuccessSnackbar(data = data)
                }
            }
        ) {
            DiyScanBatteryScreen(
                onBackClicked = navigateToMapScreen,
                onFlashLightClicked = {
                    viewModel.onEvent(DiyScanBatteryIntent.OnFlashLightClicked)
                },
                onScanCompleted = { result, isManualEntry ->
                    viewModel.onEvent(DiyScanBatteryIntent.OnScanCompleted(result,isManualEntry))
                },
                onCustomerSupportClicked = {
                    viewModel.showCustomerSupportBottomSheet()
                },
                isFlashLightOn = state.value.isFlashLightOn,
                isLoading = state.value.isLoading,
                isMultiBatteryFlow = state.value.isMultiBatteryFlow,
                scannedBatteryCount = state.value.batteryQrList.size,
                totalBatteryCount = state.value.totalBatteryCount,
            )
        }
    }*/
}

@Composable
fun TagBatteryBottomSheetHost(
    onDismiss: () -> Unit,
    onReceiveCallClicked: (String) -> Unit,
    bottomSheet: TagBatteryBottomSheet,
) {
    if(bottomSheet != TagBatteryBottomSheet.None) {
        when(bottomSheet) {
            is TagBatteryBottomSheet.None -> {}
            is TagBatteryBottomSheet.GetCallbackBottomSheet -> {
                GetCallbackContentModalBottomSheet(
                    mobileNumber = bottomSheet.mobileNumber,
                    isLoading = bottomSheet.isLoading,
                    onDismiss = onDismiss,
                    onReceiveCallClicked = onReceiveCallClicked
                )
            }
        }
    }
}

