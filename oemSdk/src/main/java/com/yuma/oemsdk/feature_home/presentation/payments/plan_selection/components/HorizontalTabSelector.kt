package com.yumaoem.feature_home.presentation.payments.plan_selection.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.color_f5f7fa
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.swap_in_progress_screen.components.circularGlow.clickable


@Composable
fun HorizontalTabSelector(
    options: List<String>,
    selectedTabIndex: Int,
    modifier: Modifier = Modifier,
    onTabSelected: (Int) -> Unit
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(color_f5f7fa)
            .padding(4.dp)
    ) {
        options.forEachIndexed { index, text ->
            val isSelected = index == selectedTabIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(LocalDimensions.current.dimen40dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (isSelected) Color.Black else Color.Transparent)
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    modifier = Modifier,
                    textAlign = TextAlign.Center,
                    text = text,
                    color = if (isSelected) Color.White else Color.Gray,
                    style = LocalTypography.current.smallBodySemiBold.copy(
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

@Preview
@Composable
fun HorizontalTabSelectorPreview(){
    YumaAppTheme {
        HorizontalTabSelector(
            options = listOf("7 Days", "30 Days"),
            selectedTabIndex = 1,
        ) {

        }
    }
}