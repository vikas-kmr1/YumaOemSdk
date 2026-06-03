package com.yumaoem.core_ui.components.step_indicator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.color.color_EEA12E

@Composable
fun StepIndicatorLine(
    isActive: Boolean,
    modifier: Modifier = Modifier,
    activeColor: Color = LocalColors.current.primary[Colors.TYPE_500.ordinal],
    inactiveColor: Color = LocalColors.current.neutral[Colors.TYPE_300.ordinal],
    height: Dp = 6.dp,
    cornerRadius: Dp = 8.dp,
    isLastStep: Boolean
) {
    val color = when {
        isLastStep -> color_EEA12E
        isActive -> activeColor
        else -> inactiveColor
    }
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(color)
    )
}