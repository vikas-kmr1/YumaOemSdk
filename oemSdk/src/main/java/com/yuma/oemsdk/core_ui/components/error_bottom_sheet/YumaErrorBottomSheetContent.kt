package com.yumaoem.core_ui.components.error_bottom_sheet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yumaoem.core_ui.components.buttons.YumaPrimaryButton
import com.yumaoem.core_ui.components.text_field.TrailingIcon
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.typography.LocalTypography

@Composable
fun YumaErrorBottomSheetContent(
    modifier: Modifier = Modifier
        .padding(horizontal = 24.dp, vertical = 20.dp)
        .background(Color.White, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
    imageRes: Painter? = null,
    imageSize: Dp = LocalDimensions.current.dimen40dp,
    title: String? = null,
    titleStyle: TextStyle = LocalTypography.current.bodyLargeSemiBold,
    titleColor: Color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
    subtitle: String? = null,
    subtitleStyle: TextStyle = LocalTypography.current.bodyMedium,
    subtitleColor: Color = LocalColors.current.neutral[Colors.TYPE_700.ordinal],
    buttonText: String? = null,
    onButtonClick: (() -> Unit)? = null,
    trailingIconRes: Painter? = null,
    showDivider: Boolean = false,
    backgroundColor: Color = Color.White,
    topSpacerHeight: Dp = LocalDimensions.current.dimen24dp,
    betweenImageAndTitle: Dp = LocalDimensions.current.dimen16dp,
    betweenTitleAndSubtitle: Dp = LocalDimensions.current.dimen8dp,
    bottomSpacerHeight: Dp = 36.dp
) {
    Column(
        modifier = modifier
            .background(backgroundColor)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(topSpacerHeight))
        imageRes?.let {
            Image(
                painter = it,
                contentDescription = null,
                modifier = Modifier.size(imageSize)
            )
            Spacer(modifier = Modifier.height(betweenImageAndTitle))
        }

        title?.let {
            Text(
                text = it,
                style = titleStyle,
                color = titleColor,
                textAlign = TextAlign.Center
            )
        }
        subtitle?.let {
            Spacer(modifier = Modifier.height(betweenTitleAndSubtitle))
            Text(
                text = it,
                style = subtitleStyle,
                color = subtitleColor,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(bottomSpacerHeight))

        if (showDivider) {
            Divider(color = LocalColors.current.neutral[Colors.TYPE_300.ordinal])
            Spacer(modifier = Modifier.height(LocalDimensions.current.dimen16dp))
        }

        if (buttonText != null && onButtonClick != null) {
            YumaPrimaryButton(
                buttonText = buttonText,
                onClick = onButtonClick,
                trailingIcon = {
                    trailingIconRes?.let {
                        TrailingIcon(trailingIcon = it) {}
                    }
                }
            )
        }
    }
}
