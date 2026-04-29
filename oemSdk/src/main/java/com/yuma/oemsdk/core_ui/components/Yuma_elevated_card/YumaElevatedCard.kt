package com.yumaoem.core_ui.components.Yuma_elevated_card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yumaoem.core_ui.theme.YumaAppTheme

@Composable
fun YumaElevatedCard(
    containerColor: Color,
    shadowColor: Color,
    paddingValues: PaddingValues = PaddingValues(bottom = 4.dp,top = 0.dp,start = 0.dp,end = 0.dp),
    modifier: Modifier = Modifier,
    cardCornerRadius: Dp = YumaAppTheme.dimensions.yumaCardCornerRadius,
    content: @Composable ColumnScope.() -> Unit
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(cardCornerRadius))
            .background(containerColor)
            .padding(paddingValues),
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(cardCornerRadius))
                .background(shadowColor),
            content = content
        )
    }
}

@Composable
fun YumaElevatedCard2(
    containerColor: Color,
    shadowColor: Color,
    paddingValues: PaddingValues = PaddingValues(bottom = 4.dp,top = 1.dp,start = 1.dp,end = 1.dp),
    modifier: Modifier = Modifier,
    cardCornerRadius: Dp = YumaAppTheme.dimensions.yumaCardCornerRadius,
    content: @Composable ColumnScope.() -> Unit
){
    Box(
        modifier = Modifier
            .wrapContentWidth()
            .clip(RoundedCornerShape(cardCornerRadius))
            .background(containerColor)
            .padding(paddingValues),
    ) {
        Column(
            modifier = modifier
                .wrapContentWidth()
                .clip(RoundedCornerShape(cardCornerRadius))
                .background(shadowColor),
            content = content
        )
    }
}