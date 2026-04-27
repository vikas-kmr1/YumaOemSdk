/*
package com.yumaoem.feature_home.presentation.diy_flow.scan_qr

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier

import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

import com.yumaoem.core.utils.global_events.HideBottomBar
import com.yumaoem.core.utils.global_events.ShowBottomBar
import com.yumaoem.core.utils.global_events.bottom_bar_event.BottomBarEventController
import com.yumaoem.core.utils.noRippleDebounceClickable
=
import com.yumaoem.core_ui.components.snackbar.SuccessSnackbar

import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.core_ui.utils.animation.defaultEnterTransition
import com.yumaoem.core_ui.utils.animation.defaultExitTransition
import com.yumaoem.core_ui.utils.animation.defaultPopEnterTransition
import com.yumaoem.core_ui.utils.animation.defaultPopExitTransition
import com.yumaoem.core_ui.utils.snackbar.SnackbarController
import com.yumaoem.core_ui.utils.snackbar.SnackbarEvent
import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.DiySwapBottomSheet
import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.components.GetCallbackContentModalBottomSheet
import com.yumaoem.feature_home.presentation.diy_flow.error_bottom_sheets.IncorrectModalBottomSheet
import com.yumaoem.feature_home.presentation.diy_flow.scan_illustration_screen.DiyScanIllustrationScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import qrscanner.CameraLens
import qrscanner.QrScanner

val scope = CoroutineScope(SupervisorJob()+ Dispatchers.Main)

@OptIn(ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun ScanMachineQrScreenRoot(
    navigateToSwapInProgress: (String) -> Unit,
    onTokenCheckInReverted: () -> Unit,
    isHomeTab: Boolean
) {
    val viewModel = koinViewModel<ScanQrViewModel>()
    val uiState = viewModel.uiState.collectAsState()
    val showIllustrationScreen = uiState.value.showIllustrationScreen

    val showBlank = remember {
        mutableStateOf(true)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        scope.launch {
            delay(200)
            showBlank.value = false
        }
        onDispose {
            viewModel.onEvent(ScanQrEvent.Disposed)
        }
    }

    BackHandler {
        viewModel.onEvent(ScanQrEvent.OnBackClicked)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is QrScannerUiEvent.ShowError -> {
                    SnackbarController.sendEvent(SnackbarEvent(message = event.message))
                }

                is QrScannerUiEvent.NavigateToSwapInProgress -> {
                    navigateToSwapInProgress(event.result)
                }

                QrScannerUiEvent.OnTokenCheckInReverted -> {
                    onTokenCheckInReverted()
                }

                is QrScannerUiEvent.ShowSuccessSnackbar -> {
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

    LaunchedEffect(Unit) {
        viewModel.sendScanYcuScreenViewed()
    }


    if (isHomeTab){
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data ->
                    SuccessSnackbar(data = data)
                }
            }
        )
        {
            if (showBlank.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                )
            } else {
                AnimatedContent(
                    modifier = Modifier,
                    targetState = showIllustrationScreen,
                    transitionSpec = {
                        if (targetState) {
                            defaultPopEnterTransition() with defaultPopExitTransition()
                        } else {
                            defaultEnterTransition() with defaultExitTransition()
                        }
                    },
                    label = "ScanScreenTransition"
                ) { showIllustration ->
                    if (showIllustration) {
                        DiyScanIllustrationScreen(
                            onNextClicked = { viewModel.changeShowIllustrationScreenStatus(status = false) }
                        )
                        scope.launch {
                            BottomBarEventController.sendEvent(ShowBottomBar)
                        }
                    } else {
                        ScanMachineQrScreen(
                            bottomSheet = uiState.value.bottomSheet,
                            onBackClicked = { viewModel.onEvent(ScanQrEvent.OnBackClicked) },
                            onFlashLightClicked = { viewModel.onEvent(ScanQrEvent.ToggleFlashlight) },
                            onScanCompleted = { qrCode, isQrScan ->
                                viewModel.onEvent(ScanQrEvent.OnScanCompleted(qrCode,isQrScan))
                            },
                            onRetryScanClicked = {
                                viewModel.onEvent(ScanQrEvent.DismissBottomSheet)
                            },
                            isFlashLightOn = uiState.value.isFlashlightOn,
                            onReceiveCallClicked = {
                                viewModel.onEvent(ScanQrEvent.OnAutoDialerRequestReceived(it))
                            },
                            onDismissBottomSheet = {
                                viewModel.onEvent(ScanQrEvent.DismissBottomSheet)
                            },
                            onCustomerSupportClicked = {
                                viewModel.onEvent(ScanQrEvent.OnCustomerSupportClicked)
                            }
                        )
                        scope.launch {
                            BottomBarEventController.sendEvent(HideBottomBar)
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun ScanMachineQrScreen(
    bottomSheet: ScanQrUiStateBottomSheet,
    modifier: Modifier = Modifier,
    onRetryScanClicked:()-> Unit,
    onBackClicked: () -> Unit = {},
    onFlashLightClicked: () -> Unit = {},
    onScanCompleted: (String, Boolean) -> Unit,
    onReceiveCallClicked: (String) -> Unit,
    onCustomerSupportClicked: () -> Unit,
    onDismissBottomSheet: () -> Unit = {},
    isFlashLightOn: Boolean = false
) {
    var qrNumber by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            QrScanner(
                modifier = Modifier
                    .fillMaxSize(),
                flashlightOn = isFlashLightOn,
                cameraLens = CameraLens.Back,
                openImagePicker = false,
                onCompletion = { onScanCompleted(it,true) },
                onFailure = {},
                imagePickerHandler = {},
                customOverlay = {
                    drawQRScannerOverlay()
                }
            )

            ScannerScreenHeader(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .align(Alignment.TopCenter),
                isFlashLightOn = isFlashLightOn,
                onBackClicked = onBackClicked,
                onFlashLightClicked = onFlashLightClicked
            )

            QRNumberTextField(
                value = qrNumber,
                onValueChange = { qrNumber = it },
                onEnterPressed = {
                    println("Enter pressed with value: $qrNumber")
                    onScanCompleted(qrNumber,false)
                },
                keyboardIcon = painterResource(Res.drawable.ic_keyboard_alt),
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(start = 32.dp, end = 32.dp, top = 100.dp)
                    .width(200.dp)
            )

            DiySwapNeedHelpFooter(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .wrapContentHeight(),
                onCustomerSupportClicked = onCustomerSupportClicked
            )
        }
    }

    QrScanBottomSheetHost(
        onDismiss = onDismissBottomSheet,
        onReceiveCallClicked = onReceiveCallClicked,
        bottomSheet = bottomSheet,
        onRetryScanClicked = onRetryScanClicked
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScanBottomSheetHost(
    onDismiss: ()-> Unit,
    onReceiveCallClicked: (String) -> Unit,
    bottomSheet: ScanQrUiStateBottomSheet,
    onRetryScanClicked: () -> Unit
) {
    if (bottomSheet != DiySwapBottomSheet.None) {
        when (bottomSheet) {
            is ScanQrUiStateBottomSheet.GetCallbackBottomSheet -> {
                GetCallbackContentModalBottomSheet(
                    mobileNumber = bottomSheet.mobileNumber,
                    isLoading = bottomSheet.isLoading,
                    onDismiss = onDismiss,
                    onReceiveCallClicked = onReceiveCallClicked
                )
            }
            ScanQrUiStateBottomSheet.IncorrectQRModalBottomSheet -> {
                IncorrectModalBottomSheet(
                    onRetry = onRetryScanClicked
                )
            }
            ScanQrUiStateBottomSheet.None -> {}
        }
    }
}

@Composable
fun ScannerScreenHeader(
    modifier: Modifier,
    isFlashLightOn: Boolean,
    onBackClicked: () -> Unit,
    onFlashLightClicked: () -> Unit,
) {
    val flashIcon = if (isFlashLightOn) {
        painterResource(Res.drawable.ic_flash_on)
    } else {
        painterResource(Res.drawable.ic_flash_off)
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .padding(horizontal = 26.dp, vertical = 40.dp)
            .fillMaxWidth()
    ) {

        Image(
            painter = painterResource(Res.drawable.ic_go_back),
            contentDescription = "",
            modifier = Modifier
                .padding(bottom = 20.dp, end = 20.dp)
                .noRippleDebounceClickable {
                    onBackClicked()
                }
        )
        Image(
            painter = flashIcon,
            contentDescription = "",
            modifier = Modifier
                .noRippleDebounceClickable {
                    onFlashLightClicked()
                }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanQrCodeBottomSheet() {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showSheet by remember { mutableStateOf(true) }
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = {},
            sheetState = sheetState
        ) {
            ScannerQrCodeBottomSheetContent()
        }
    }
}

@Composable
fun ScannerQrCodeBottomSheetContent(
    modifier: Modifier = Modifier,
    onSizeChanged: (IntSize) -> Unit = {}
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .fillMaxWidth()
            .background(Color.White)
            .onSizeChanged { size -> onSizeChanged(size) }, // Measure the size
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Scan QR on the\n machine",
            textAlign = TextAlign.Center,
            style = LocalTypography.current.bodyLargeSemiBold.copy(
                color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
            )
        )
        Spacer(modifier = Modifier.height(22.dp))
        YcuImage()
        Spacer(modifier = Modifier.height(26.dp))
    }
}

@Composable
fun YcuImage(
    imageUrl: String = "https://yuma-static.s3.ap-south-1.amazonaws.com/gen5/ycu_scan_illustration.png"
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = "ycu picture",
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .size(200.dp),
        contentScale = ContentScale.FillBounds
    )
}

*/
