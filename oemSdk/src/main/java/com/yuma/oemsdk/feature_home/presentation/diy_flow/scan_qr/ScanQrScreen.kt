package com.yumaoem.feature_home.presentation.diy_flow.scan_qr


import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.yuma.oemsdk.R
import com.yuma.oemsdk.YumaSdk
import com.yuma.oemsdk.core_ui.utils.ImageConstants
import com.yuma.oemsdk.feature_home.presentation.diy_flow.scan_qr.drawQRScannerOverlay
import com.yumaoem.core.utils.global_events.HideBottomBar
import com.yumaoem.core.utils.global_events.ShowBottomBar
import com.yumaoem.core.utils.global_events.bottom_bar_event.BottomBarEventController
import com.yumaoem.core.utils.noRippleDebounceClickable
import com.yumaoem.core.utils.qr_scanner.NativeQrScanner
import com.yumaoem.core_ui.components.snackbar.SuccessSnackbar
import com.yumaoem.core_ui.components.step_indicator.StepIndicator
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.core_ui.utils.animation.defaultEnterTransition
import com.yumaoem.core_ui.utils.animation.defaultExitTransition
import com.yumaoem.core_ui.utils.animation.defaultPopEnterTransition
import com.yumaoem.core_ui.utils.animation.defaultPopExitTransition
import com.yumaoem.core_ui.utils.snackbar.SnackbarController
import com.yumaoem.core_ui.utils.snackbar.SnackbarEvent
import com.yumaoem.feature_home.presentation.diy_flow.dialogs.SwapInfoDialog
import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.components.GetCallbackContentModalBottomSheet
import com.yumaoem.feature_home.presentation.diy_flow.error_bottom_sheets.IncorrectModalBottomSheet
import com.yumaoem.feature_home.presentation.diy_flow.scan_illustration_screen.DiyScanIllustrationScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalAnimationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun ScanMachineQrScreenRoot(
    navigateToSwapInProgress: (String) -> Unit,
    onTokenCheckInReverted: () -> Unit,
    isHomeTab: Boolean,
    currentBatterySwap: Int,
    isMultiYcuSwap: Boolean
) {
    val viewModel: ScanQrViewModel = viewModel(
        factory = YumaSdk.scanQrViewModelFactory
    )

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
        if (isMultiYcuSwap) {
            viewModel.setMultiYcuArgs(isMultiYcuSwap, currentBatterySwap)
        }
        viewModel.sendScanYcuScreenViewed()
    }


    if (isHomeTab) {
        Scaffold(
            snackbarHost = {
                SnackbarHost(snackbarHostState) { data ->
                    SuccessSnackbar(data = data)
                }
            }
        )
        { innerPadding ->
            if (showBlank.value) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                )
            } else {
                AnimatedContent(
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
                        LaunchedEffect(Unit) {
                            scope.launch {
                                BottomBarEventController.sendEvent(ShowBottomBar)
                            }
                        }
                    } else {
                        ScanMachineQrScreen(
                            bottomSheet = uiState.value.bottomSheet,
                            dialog = uiState.value.dialog,
                            isMultiYcuSwap = uiState.value.isMultiYcuSwap,
                            onBackClicked = { viewModel.onEvent(ScanQrEvent.OnBackClicked) },
                            onFlashLightClicked = { viewModel.onEvent(ScanQrEvent.ToggleFlashlight) },
                            onScanCompleted = { qrCode, isQrScan ->
                                viewModel.onEvent(ScanQrEvent.OnScanCompleted(qrCode, isQrScan))
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
                            onDialogActionClicked = {
                                viewModel.onEvent(ScanQrEvent.DismissDialog)
                            },
                            onCustomerSupportClicked = {
                                viewModel.onEvent(ScanQrEvent.OnCustomerSupportClicked)
                            }
                        )
                        LaunchedEffect(Unit) {
                            scope.launch {
                                BottomBarEventController.sendEvent(HideBottomBar)
                            }
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
    dialog: ScanQrUiStateDialog,
    isMultiYcuSwap: Boolean,
    modifier: Modifier = Modifier,
    onRetryScanClicked: () -> Unit,
    onBackClicked: () -> Unit = {},
    onFlashLightClicked: () -> Unit = {},
    onScanCompleted: (String, Boolean) -> Unit,
    onReceiveCallClicked: (String) -> Unit,
    onCustomerSupportClicked: () -> Unit,
    onDismissBottomSheet: () -> Unit = {},
    onDialogActionClicked: () -> Unit,
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
            var viewfinderRect by remember { mutableStateOf<Rect?>(null) }

            Box(modifier = Modifier.fillMaxSize()) {
                NativeQrScanner(
                    modifier = Modifier.fillMaxSize(),
                    flashlightOn = isFlashLightOn,
                    onQrCodeScanned = { onScanCompleted(it, true) },
                    onFailure = {}
                )
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val canvasHeight = size.height

                    val viewfinderSize = minOf(canvasWidth, canvasHeight) * 0.6f
                    val left = (canvasWidth - viewfinderSize) / 2f
                    val top = (canvasHeight - viewfinderSize) / 2f

                    viewfinderRect = Rect(
                        left,
                        top,
                        left + viewfinderSize,
                        top + viewfinderSize
                    )
                    drawQRScannerOverlay(bottomSpace = 0.dp)
                }
            }

            ScannerScreenHeader(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .align(Alignment.TopCenter),
                isFlashLightOn = isFlashLightOn,
                onBackClicked = onBackClicked,
                onFlashLightClicked = onFlashLightClicked
            )

            viewfinderRect?.let { rect ->
                QRNumberTextField(
                    value = qrNumber,
                    onValueChange = { qrNumber = it },
                    onEnterPressed = {
                        println("Enter pressed with value: $qrNumber")
                        onScanCompleted(qrNumber, false)
                    },
                    keyboardIcon = painterResource(R.drawable.ic_keyboard_alt),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .offset {
                            IntOffset(
                                x = 0,
                                y = (rect.bottom + 24.dp.toPx()).toInt()
                            )
                        }
                        .width(220.dp)
                )
            }

        }
        DiyScanQrFooter(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            isMultiYcuSwap = isMultiYcuSwap,
            onCustomerSupportClicked = onCustomerSupportClicked
        )
    }

    QrScanBottomSheetHost(
        onDismiss = onDismissBottomSheet,
        onReceiveCallClicked = onReceiveCallClicked,
        bottomSheet = bottomSheet,
        onRetryScanClicked = onRetryScanClicked
    )
    QrScanDialogHost(
        dialog = dialog,
        onAction = onDialogActionClicked
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScanBottomSheetHost(
    onDismiss: () -> Unit,
    onReceiveCallClicked: (String) -> Unit,
    bottomSheet: ScanQrUiStateBottomSheet,
    onRetryScanClicked: () -> Unit
) {
    if (bottomSheet != ScanQrUiStateBottomSheet.None) {
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
fun DiyScanQrFooter(
    modifier: Modifier = Modifier,
    isMultiYcuSwap: Boolean,
    onCustomerSupportClicked: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0x88000000))
        )
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            if (!isMultiYcuSwap) {
                StepIndicator(
                    totalSteps = 3,
                    currentStep = 2,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            ImageIllustration()
            Spacer(modifier = Modifier.height(15.dp))
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF5F7FA)
                    )
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.Center,
            ) {
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
    }
}

@Composable
private fun ImageIllustration(
    imageUrl: String = "https://yuma-static.s3.ap-south-1.amazonaws.com/gen5/ic_scan_diy.png"
) {
    val footerText = stringResource(R.string.scan_machine_qr)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = footerText,
            modifier = Modifier,
            style = LocalTypography.current.bodyLargeSemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(5.dp))
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(160.dp),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun QrScanDialogHost(
    dialog: ScanQrUiStateDialog,
    onAction: () -> Unit
) {
    if (dialog != ScanQrUiStateDialog.None) {
        when (dialog) {
            ScanQrUiStateDialog.None -> {}
            ScanQrUiStateDialog.ScanQrCodeDialog -> {
                SwapInfoDialog(
                    title = "Battery 2",
                    onAction = onAction
                )
            }
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
        painterResource(R.drawable.ic_flash_on)
    } else {
        painterResource(R.drawable.ic_flash_off)
    }
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .padding(horizontal = 26.dp, vertical = 40.dp)
            .fillMaxWidth()
    ) {

        Image(
            painter = painterResource(R.drawable.ic_go_back),
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
    imageUrl: String = ImageConstants.YCU_SCAN_URL
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

@Preview
@Composable
fun ScanQrCodeBottomSheetContentPreview() {
    YumaAppTheme {
        DiyScanQrFooter(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            isMultiYcuSwap = false,
            onCustomerSupportClicked = {}
        )
    }
}

