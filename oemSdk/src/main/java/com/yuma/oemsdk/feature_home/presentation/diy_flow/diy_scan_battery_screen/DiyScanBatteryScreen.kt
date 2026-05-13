package com.yumaoem.feature_home.presentation.diy_flow.diy_scan_battery_screen

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yuma.oemsdk.R
import com.yuma.oemsdk.feature_home.presentation.diy_flow.scan_qr.drawQRScannerOverlay
import com.yumaoem.core.utils.noRippleDebounceClickable
import com.yumaoem.core.utils.qr_scanner.NativeQrScanner
import com.yumaoem.core_ui.components.step_indicator.StepIndicator
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.shapes.LocalAppShapes
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.feature_home.presentation.diy_flow.scan_qr.QRNumberTextField
import com.yumaoem.feature_home.presentation.diy_flow.scan_qr.ScannerScreenHeader
import qrscanner.CameraLens
import qrscanner.QrScanner

@Composable
fun DiyScanBatteryScreen(
    modifier: Modifier = Modifier,
    onBackClicked: () -> Unit = {},
    onFlashLightClicked: () -> Unit = {},
    onScanCompleted: (String, Boolean) -> Unit,
    showCustomerSupport: Boolean = false,
    showManualInputField: Boolean = true,
    onCustomerSupportClicked: () -> Unit,
    isFlashLightOn: Boolean = false,
    isLoading: Boolean = false,
    isMultiBatteryFlow:Boolean = true,
    isDischargedBatteryScan: Boolean = false,
    scannedBatteryCount: Int = 0,
    totalBatteryCount: Int = 0,
    isMultiYcuSwap: Boolean = false,
    showStepIndicator: Boolean = true
) {
    var qrNumber by remember { mutableStateOf("") }
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            var viewfinderRect by remember { mutableStateOf<Rect?>(null) }

            Box(modifier = Modifier.fillMaxSize()) {
                NativeQrScanner (
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
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp),
                isFlashLightOn = isFlashLightOn,
                onBackClicked = onBackClicked,
                onFlashLightClicked = onFlashLightClicked
            )

            if (isMultiBatteryFlow) {
                ScannedBatteryCount(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 80.dp),
                    totalBatteries = totalBatteryCount,
                    scannedBatteries = scannedBatteryCount
                )
            }
            if(showManualInputField) {
                viewfinderRect?.let { rect ->
                    QRNumberTextField(
                        value = qrNumber,
                        onValueChange = { qrNumber = it },
                        onEnterPressed = {
                            onScanCompleted(qrNumber, true)
                            qrNumber = ""
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
        }

        ScanBatteryFooter(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            isLoading = isLoading,
            isDischargedBatteryScan = isDischargedBatteryScan,
            isMultiBatteryFlow = isMultiBatteryFlow,
            onCustomerSupportClicked = onCustomerSupportClicked,
            showCustomerSupport = showCustomerSupport,
            showStepIndicator = showStepIndicator,
            isMultiYcuSwap = isMultiYcuSwap
        )
    }
}

@Composable
fun ScanBatteryFooter(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isDischargedBatteryScan: Boolean = false,
    isMultiBatteryFlow: Boolean = false,
    showCustomerSupport: Boolean = false,
    showStepIndicator: Boolean = true,
    isMultiYcuSwap: Boolean = false,
    onCustomerSupportClicked: () -> Unit
){
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
            if(!isMultiYcuSwap && showStepIndicator) {
                StepIndicator(
                    totalSteps = if (isDischargedBatteryScan) 3 else 4,
                    currentStep = if (isDischargedBatteryScan) 1 else 4,
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            if (isLoading) {
                LoadingIllustration()
            } else {
                ScanIllustration(
                    isDischargedBatteryScan = isDischargedBatteryScan,
                    isMultiBatteryFlow = isMultiBatteryFlow
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            if (showCustomerSupport) {
                DiySwapCustomerSupportFooter(
                    onCustomerSupportClicked = {
                        onCustomerSupportClicked()
                    }
                )
            }
        }
    }
}

@Composable
private fun DiySwapCustomerSupportFooter(
    modifier: Modifier = Modifier,
    onCustomerSupportClicked: () -> Unit,
){
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

@Composable
private fun LoadingIllustration(){
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
        ){
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = stringResource(R.string.scanning_battery),
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            style = LocalTypography.current.bodyMedium.copy(
                color = LocalColors.current.neutral[Colors.TYPE_600.ordinal]
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ScanIllustration(
    isDischargedBatteryScan: Boolean,
    isMultiBatteryFlow: Boolean,
    imageUrl: String = "https://yuma-static.s3.ap-south-1.amazonaws.com/oem/Frame+14458.png"
) {
    val footerText = when {
        isDischargedBatteryScan && isMultiBatteryFlow ->
            stringResource(R.string.scan_discharged_batteries)

        isDischargedBatteryScan ->
            stringResource(R.string.scan_discharged_battery)

        isMultiBatteryFlow ->
            stringResource(R.string.scan_two_batteries_message)

        else ->
            stringResource(R.string.scan_the_battery)
    }

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
fun ScannedBatteryCount(
    modifier: Modifier = Modifier,
    totalBatteries: Int,
    scannedBatteries: Int
) {
    val isFirstScanCompleted = scannedBatteries >= 1
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(shape = LocalAppShapes.current.textFieldShape)
            .background(
                color = if (isFirstScanCompleted) LocalColors.current.primary[Colors.TYPE_500.ordinal] else Color.White
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(
                R.string.battery_count_format,
                scannedBatteries,
                totalBatteries
            ),
            style = LocalTypography.current.smallBodyMedium,
            color = if (isFirstScanCompleted) Color.White else Color.Black
        )
    }
}

@Composable
@Preview
fun ScannedBatteryCountPreview() {
    YumaAppTheme {
        ScannedBatteryCount(
            totalBatteries = 2,
            scannedBatteries = 1
        )
    }
}


@Preview
@Composable
fun ScanBatteryFooterPreview() {
    YumaAppTheme {
        ScanBatteryFooter(
            isLoading = false,
            isMultiBatteryFlow = false,
            modifier = Modifier,
            onCustomerSupportClicked = {},
            showCustomerSupport = true
        )
    }
}

