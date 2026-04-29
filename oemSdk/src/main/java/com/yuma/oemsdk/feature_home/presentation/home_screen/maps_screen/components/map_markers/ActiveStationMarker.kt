package com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.map_markers

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.yuma.oemsdk.R


@Composable
fun ActiveYumaStationMarker(){
    Box {
        Image(
            painter = painterResource(R.drawable.yuma_operational_station_marker_icon),
            contentDescription = "Yuma Station Marker"
        )
    }
}