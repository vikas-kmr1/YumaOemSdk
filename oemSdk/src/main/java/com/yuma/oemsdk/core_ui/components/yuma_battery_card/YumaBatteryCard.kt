package com.yumaoem.core_ui.components.yuma_battery_card

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.yuma.oemsdk.core_ui.utils.ImageConstants
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.typography.LocalTypography


@Composable
fun YumaBatteryCard(
    isDisabled: Boolean = false,
    titleText: String? = null,
    titleTextColor: Color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
    titleTextStyle: TextStyle = LocalTypography.current.smallBodySemiBold,
    titleTextBottomPadding: Dp = 14.5.dp,
    backGroundColor: Color = Color.White,
    imageUrl: String? = ImageConstants.YCU_BATTERY_URL,
    imageDrawableWidth : Dp = 0.dp,
    imageDrawableHeight : Dp = 0.dp,
    bottomText: String,
    bottomTextColor: Color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
    bottomTextStyle: TextStyle = LocalTypography.current.bodyMedium,
    bottomTextTopPadding: Dp = 20.dp,
    borderStrokeWidth: Dp = 0.dp,
    borderColor: Color,
    borderRadius: Dp = 16.dp,
    topSpacer: Dp = 16.dp,
    bottomSpacer: Dp = 18.5.dp
) {
    val shape = RoundedCornerShape(borderRadius)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(borderStrokeWidth, borderColor, shape)
            .background(backGroundColor, shape),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(topSpacer))
        if (titleText != null) {
            Text(
                modifier = Modifier.padding(
                    bottom = titleTextBottomPadding
                ),
                text = titleText,
                style = titleTextStyle,
                color = titleTextColor
            )
        }


        AsyncImage(
            model = imageUrl,
            contentDescription = "ycu battery image",
            modifier = Modifier
                .width(imageDrawableWidth)
                .height(imageDrawableHeight),
            contentScale = ContentScale.FillBounds,
            alpha = if (isDisabled) 0.5f else 1f,
            colorFilter = if (isDisabled) {
                ColorFilter.colorMatrix(
                    ColorMatrix().apply { setToSaturation(0f) }
                )
            } else null
        )


        Text(
            modifier = Modifier
                .padding(
                     top = bottomTextTopPadding
                ).background(
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp)
                ).padding(
                    vertical = 3.dp,
                    horizontal = 14.5.dp
                ),
            text = bottomText,
            style = bottomTextStyle,
            color = bottomTextColor,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(bottomSpacer))
    }
}

@Preview
@Composable
fun YumaBatteryCardPreview() {
    YumaAppTheme {
        YumaBatteryCard(
            titleText = "Battery 1",
            imageUrl = ImageConstants.YCU_BATTERY_URL,
            imageDrawableWidth = 62.dp,
            imageDrawableHeight = 108.dp,
            backGroundColor = Color.White,
            bottomText = "Machine 1",
            borderStrokeWidth = 1.dp,
            borderColor = Color.Gray
        )
    }
}
