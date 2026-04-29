package com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.map_markers

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.yuma.oemsdk.R

@Composable
fun InActiveYumaStationMarker(){
    Box {
        Image(
            painter = painterResource(R.drawable.yuma_inactive_station_marker),
            contentDescription = "Yuma Station Marker"
        )
    }
}