package com.yumaoem.feature_home.presentation.diy_flow.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.yuma.oemsdk.R
import com.yuma.oemsdk.core_ui.utils.ImageConstants
import com.yumaoem.core_ui.components.yuma_battery_card.YumaBatteryCard
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.typography.LocalTypography
import kotlinx.coroutines.delay



@Composable
fun MultiYcuInfoDialog(
    title: String,
    onAction: () -> Unit
) {
    var buttonEnabled by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = {},
        content = {
            AppDialog(
                titleText = title,
                imageComposable = {TwoBatteryImageComposable()},
                description = listOf("You will get batteries from different machines."),
                buttonText = "Continue",
                buttonTrailingIcon = {
                    ContinueButtonTimer(
                        totalSeconds = 5
                    ) {
                        buttonEnabled = true
                    }
                },
                buttonEnabled = buttonEnabled,
                onButtonClick = onAction,
                showStepIndicator = true
            )
        }
    )
}

@Composable
fun ContinueButtonTimer(
    totalSeconds: Int = 5,
    onFinished: () -> Unit
) {
    var remainingTime by remember { mutableStateOf(totalSeconds) }

    LaunchedEffect(Unit) {
        while (remainingTime > 0) {
            delay(1_000L)
            remainingTime--
        }
        onFinished()
    }

    Text(
        text = if (remainingTime > 0) " (${remainingTime}s)" else "",
        textAlign = TextAlign.Center,
        style = LocalTypography.current.bodyMedium,
        color = Color.White
    )
}


@Composable
fun TwoBatteryImageComposable() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            YumaBatteryCard(
                titleText = "Battery 1",
                imageUrl = ImageConstants.YCU_BATTERY_URL,
                imageDrawableWidth = 62.dp,
                imageDrawableHeight = 108.dp,
                bottomText = "Machine 1",
                borderColor = Color.White
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Icon(
            modifier = Modifier.size(
                25.dp
            ),
            painter = painterResource(R.drawable.arrow_right),
            contentDescription = "right arrow",
        )
        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            YumaBatteryCard(
                titleText = "Battery 2",
                imageUrl = ImageConstants.YCU_BATTERY_URL,
                imageDrawableWidth = 62.dp,
                imageDrawableHeight = 108.dp,
                bottomText = "Machine 2",
                borderColor = Color.White
            )
        }

    }
}

@Preview
@Composable
private fun TwoBatteryImageComposablePreview1(){
    YumaAppTheme {
        MultiYcuInfoDialog("2 Machine Swap"){}
    }
}