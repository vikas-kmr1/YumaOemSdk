package com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.diy_multi_ycu_swap_in_progress

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.yuma.oemsdk.R
import com.yuma.oemsdk.core_ui.utils.ImageConstants
import com.yuma.oemsdk.feature_home.presentation.diy_flow.diy_swap_in_progress.DiySwapInProgressScreenStationHeader
import com.yuma.oemsdk.feature_home.presentation.diy_flow.diy_swap_in_progress.DiySwapNeedHelpFooter
import com.yumaoem.core_ui.components.buttons.YumaPrimaryButton
import com.yumaoem.core_ui.components.yuma_battery_card.YumaBatteryCard
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.feature_home.presentation.diy_flow.dialogs.MultiYcuInfoDialog
import com.yumaoem.feature_home.presentation.diy_flow.dialogs.SwapInfoDialog
import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.DiySwapDialog
import com.yumaoem.feature_home.presentation.diy_flow.diy_swap_in_progress.DiySwapInProgressState
import com.yumaoem.feature_home.presentation.profile_screen.components.BikeDetailsBottomSheet


@Composable
fun DiyMultiYcuSwapInProgressScreen(
    state: DiySwapInProgressState,
    isSubmitButtonVisible: Boolean,
    isSubmitting: Boolean,
    onSubmitButtonClicked: () -> Unit,
    onCustomerSupportClicked: () -> Unit,
    toggleBottomSheet: () -> Unit
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
                text = "Battery ${state.currentBatteryIndex}",
                style = LocalTypography.current.bodyLargeSemiBold.copy(
                    color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
                )
            )
            Spacer(modifier = Modifier.height(22.dp))
            if (state.currentBatteryIndex == 1) {
                Text(
                    modifier = Modifier.background(
                        color = LocalColors.current.primary[Colors.TYPE_300.ordinal],
                        shape = RoundedCornerShape(16.dp)
                    )
                        .padding(
                            vertical = 5.5.dp,
                            horizontal = 19.5.dp
                        ),
                    textAlign = TextAlign.Center,
                    text = emphasizedText(
                        "Do ",
                        "NOT ",
                        "insert 2nd battery"
                    ),
                    style = LocalTypography.current.smallBodyMedium.copy(
                        color = LocalColors.current.red[Colors.TYPE_500.ordinal]
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            DiySwapInProgressScreenStationHeader(
                stationName = state.stationName,
                stationId = state.stationId,
                modifier = Modifier.fillMaxWidth(),
                toggleBottomSheet = toggleBottomSheet,
                hideStepIndicator = true,
                isMultiYcuSwap = true
            )

            DiyMultiSwapProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                activeBatteryIndex = state.currentBatteryIndex
            )
        }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.Bottom
        ) {
            if (isSubmitButtonVisible) {
                YumaPrimaryButton(
                    buttonText = "Continue",
                    onClick = onSubmitButtonClicked,
                    isLoading = isSubmitting,
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
fun DiyMultiSwapProgressIndicator(
    modifier: Modifier,
    activeBatteryIndex: Int,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF5F7FA)
            )
            .padding(
                start = 40.dp,
                end = 40.dp,
                top = 13.dp,
                bottom = 28.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            BatteryCardItem(
                isActive = activeBatteryIndex == 1,
                bottomText = "Machine 1"
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            modifier = Modifier.size(
                21.dp
            ),
            painter = painterResource(R.drawable.arrow_right),
            contentDescription = "right arrow",
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            BatteryCardItem(
                isActive = activeBatteryIndex == 2,
                bottomText = "Machine 2"
            )
        }
    }
}


@Composable
private fun BatteryCardItem(
    isActive: Boolean,
    bottomText: String
) {
    val borderColor = if (isActive) {
        LocalColors.current.neutral[Colors.TYPE_900.ordinal]
    } else {
        LocalColors.current.neutral[Colors.TYPE_300.ordinal]
    }

    val borderWidth = if (isActive) 1.5.dp else 1.dp
    val backgroundColor = if (isActive) Color.White else Color.Transparent
    val bottomTextColor =
        if (isActive) LocalColors.current.neutral[Colors.TYPE_900.ordinal] else LocalColors.current.neutral[Colors.TYPE_500.ordinal]


    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        YumaBatteryCard(
            imageUrl = ImageConstants.YCU_BATTERY_URL,
            imageDrawableWidth = 40.dp,
            imageDrawableHeight = 69.dp,
            backGroundColor = backgroundColor,
            bottomText = bottomText,
            borderColor = borderColor,
            borderStrokeWidth = borderWidth,
            bottomTextColor = bottomTextColor,
            isDisabled = !isActive,
            topSpacer = 10.dp,
            bottomSpacer = 12.dp
        )
    }
}


@Composable
fun emphasizedText(
    normalPrefix: String,
    emphasized: String,
    normalSuffix: String
) = buildAnnotatedString {
    append(normalPrefix)
    withStyle(
        LocalTypography.current.smallBodySemiBold.toSpanStyle().copy(
            color = LocalColors.current.red[Colors.TYPE_500.ordinal],
            fontWeight = FontWeight.W600
        )
    ) {
        append(emphasized)
    }
    append(normalSuffix)
}


@Composable
fun DiySwapDialogHost(
    dialog: DiySwapDialog,
    onAction: () -> Unit
) {
    if (dialog != DiySwapDialog.None) {
        when (dialog) {
            DiySwapDialog.MultiYcuSwapDialog -> {
                MultiYcuInfoDialog(
                    title = "2 Machine Swap",
                    onAction = onAction
                )
            }

            DiySwapDialog.SwapInfoDialog -> {
                SwapInfoDialog(
                    title = "Battery 2",
                    onAction = onAction
                )
            }

            DiySwapDialog.None -> {}
        }
    }
}

@Preview
@Composable
fun DiyMultiYcuSwapInProgressScreenPreview() {
    YumaAppTheme {
        DiyMultiYcuSwapInProgressScreen(
            state = DiySwapInProgressState(
                stationName = "Yogananda road",
                stationId = "313",
            ),
            isSubmitButtonVisible = true,
            isSubmitting = false,
            onSubmitButtonClicked = {},
            onCustomerSupportClicked = {},
            toggleBottomSheet = {}
        )
    }
}