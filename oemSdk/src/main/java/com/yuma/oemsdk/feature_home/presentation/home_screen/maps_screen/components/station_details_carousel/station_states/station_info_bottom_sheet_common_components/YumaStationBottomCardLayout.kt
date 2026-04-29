package com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.station_info_bottom_sheet_common_components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.yumaoem.core_ui.theme.dimension.LocalDimensions

@Composable
fun YumaStationBottomCardLayout(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
    actionButton: @Composable () -> Unit
) {
    val dimensions = LocalDimensions.current
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                topStart = dimensions.yumaCardCornerRadius,
                topEnd = dimensions.yumaCardCornerRadius
                )
            )
            .background(Color.White)
            .padding(
                vertical = dimensions.dimen24dp
            )
    ) {
        content()
        Spacer(modifier = Modifier.height(dimensions.dimen24dp))
        actionButton()
    }
}
