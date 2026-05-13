package com.yumaoem.core_ui.components.step_indicator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.typography.LocalTypography


@Composable
fun StepIndicator(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier,
    spacing: Dp = 5.dp
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing)
        ) {
            repeat(totalSteps) { index ->
                StepIndicatorLine(
                    isActive = index < currentStep,
                    isLastStep = (currentStep == 4 && index == currentStep-1),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (currentStep != 4) {
            Text(
                text = "STEP $currentStep OF $totalSteps",
                style = LocalTypography.current.bodySemiBold,
                color = LocalColors.current.neutral[Colors.TYPE_500.ordinal]
            )
        }
    }
}

@Preview
@Composable
fun StepIndicatorPreview() {
    YumaAppTheme {
        StepIndicator(
            totalSteps = 3,
            currentStep = 1
        )
    }
}