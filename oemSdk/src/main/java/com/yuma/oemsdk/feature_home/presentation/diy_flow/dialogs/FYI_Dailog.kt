package com.yumaoem.feature_home.presentation.diy_flow.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yuma.oemsdk.core_ui.utils.ImageConstants
import com.yumaoem.core_ui.components.buttons.YumaPrimaryButton
import com.yumaoem.core_ui.components.step_indicator.StepIndicator
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.color.color_F5F7FA
import com.yumaoem.core_ui.theme.typography.LocalTypography


@Composable
fun AppDialog(
    titleText: String,
    titleTextColor: Color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
    titleTextStyle: TextStyle = LocalTypography.current.heading5SemiBold,
    imageComposable: @Composable () -> Unit,
    description: List<String>,
    descriptionTextColor: Color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
    descriptionTextStyle: TextStyle = LocalTypography.current.bodyMedium,
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    buttonEnabled: Boolean = true,
    buttonTrailingIcon: @Composable (() -> Unit)? = null,
    verticalPadding: Dp = 29.dp,
    horizontalPadding: Dp = 21.dp,
    spacerHeight: Dp = 30.dp,
    dialogHeight: Dp = 556.dp,
    dialogWidth: Dp = 319.dp,
    showStepIndicator: Boolean = false,
) {
    Column(
        modifier = Modifier
            .width(dialogWidth)
            .clip(shape = RoundedCornerShape(16.dp))
            .background(color = color_F5F7FA)
            .padding(
                horizontal = horizontalPadding,
                vertical = verticalPadding
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if(showStepIndicator) {
            StepIndicator(
                totalSteps = 3,
                currentStep = 3,
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = titleText,
            style = titleTextStyle,
            color = titleTextColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(spacerHeight))
        imageComposable()
        Spacer(modifier = Modifier.height(spacerHeight))
        description.forEach { it ->
            Text(
                text = it,
                color = descriptionTextColor,
                style = descriptionTextStyle,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            if(it != description.last()){
                Spacer(modifier = Modifier.height(9.dp))
            }
        }
        Spacer(modifier = Modifier.height(spacerHeight))
        if (buttonText != null && onButtonClick != null) {
            YumaPrimaryButton(
                enabled = buttonEnabled,
                buttonText = buttonText,
                onClick = onButtonClick,
                trailingIcon = buttonTrailingIcon,
                isTrailingIconVisible = true
            )
        }
    }
}


// PRE-VIEWS

@Preview
@Composable
fun AppDialogPreview() {
    YumaAppTheme {
        AppDialog(
            titleText = "2 Machine Swap",
            imageComposable = {
                TwoBatteryImageComposable()
            },
            description = listOf(
                "You will get batteries from different machines."
            ),
            buttonText = "Continue",
            onButtonClick = {},
            showStepIndicator = true
        )
    }
}

@Composable
fun ImageComposableDemo() {
    Column {
        AsyncImage(
            model = ImageConstants.YCU_SCAN_URL,
            contentDescription = "Scan Image",
            modifier = Modifier.size(162.dp)
        )
    }
}

@Preview
@Composable
fun AppDialogPreview2() {
    YumaAppTheme {
        AppDialog(
            titleText = "Battery 2",
            imageComposable = {
                ImageComposableDemo()
            },
            description = listOf(
                "1. Scan another machine",
                "2. Insert 2nd Battery"
            ),
            buttonText = "Continue",
            onButtonClick = {},
            verticalPadding = 28.dp,
            spacerHeight = 24.dp
        )
    }
}


