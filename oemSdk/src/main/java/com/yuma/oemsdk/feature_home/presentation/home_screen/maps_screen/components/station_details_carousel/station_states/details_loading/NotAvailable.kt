package com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.details_loading

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.shapes.LocalAppShapes
import com.yumaoem.core_ui.utils.shimmer.shimmerEffect

@Composable
fun StationStatusLoading() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(102.dp)
            .padding(horizontal = 4.dp)
            .clip(LocalAppShapes.current.chipShape)
            .shimmerEffect()
            .padding(all = LocalDimensions.current.dimen20dp)
    ) {

    }
}