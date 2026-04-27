package com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.station_info_bottom_sheet_common_components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yuma.oemsdk.R
import com.yumaoem.core.utils.noRippleDebounceClickable


@Composable
fun GoogleMapLogoIcon(
    onDirectionsClicked: () -> Unit
) {
    Image(
        modifier = Modifier
            .size(48.dp)
            .noRippleDebounceClickable { onDirectionsClicked() },
        painter = painterResource(R.drawable.ic_directions_rounded_corners),
        contentDescription = "Directions"
    )
}