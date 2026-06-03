package com.yumaoem.core_ui.components.buttons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors

@Composable
fun YumaSecondaryButton(
    enabled: Boolean = true,
    buttonText:String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isTrailingIconVisible: Boolean = false,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    buttonTextColor: Color = LocalColors.current.red[Colors.TYPE_500.ordinal],
    onClick: () ->Unit,
){
    val shadowColor =
        if (enabled) YumaAppTheme.colors.neutral[Colors.TYPE_300.ordinal]
        else YumaAppTheme.colors.neutral[Colors.TYPE_300.ordinal]

    val containerColor =
        if (enabled) Color.White
        else Color.White

    YumaButtonInternal(
        modifier =  modifier,
        containerColor = containerColor,
        shadowColor = shadowColor,
        isLoading = isLoading,
        onClick = onClick,
        enabled =  enabled,
        buttonTextColor = buttonTextColor,
        buttonText = buttonText,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        isTrailingIconVisible = isTrailingIconVisible,
        paddingValues = PaddingValues(bottom = 4.dp,top = 1.dp,start = 1.dp,end = 1.dp)
    )
}