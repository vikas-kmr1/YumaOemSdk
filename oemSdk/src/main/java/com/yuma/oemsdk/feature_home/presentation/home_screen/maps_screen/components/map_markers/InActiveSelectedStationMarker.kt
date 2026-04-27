package com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.map_markers

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.yuma.oemsdk.R


@Composable
fun InActiveSelectedYumaStationMarker(
    stationDistance: String,
    travelDuration:String
){
    Box {
        MarkerBackgroundChip(
            stationDistance = stationDistance,
            travelDuration =  travelDuration
        )
        Image(
            painter = painterResource(R.drawable.yuma_inactive_station_marker),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterStart)
        )
    }
}
