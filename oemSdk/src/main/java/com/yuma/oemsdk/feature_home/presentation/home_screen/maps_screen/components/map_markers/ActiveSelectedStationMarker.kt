package com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.map_markers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yuma.oemsdk.R
import com.yumaoem.core_ui.components.Yuma_elevated_card.YumaElevatedCard
import com.yumaoem.core_ui.components.Yuma_elevated_card.YumaElevatedCard2
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.typography.LocalTypography


@Composable
fun ActiveSelectedYumaStationMarker(
    stationDistance: String,
    travelDuration:String
){
    Box {
        MarkerBackgroundChip(
            stationDistance = stationDistance,
            travelDuration =  travelDuration
        )
        Image(
            painter = painterResource(R.drawable.yuma_operational_station_marker_icon),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
        )
    }
}

@Composable
internal fun MarkerBackgroundChip(
    stationDistance: String,
    travelDuration:String
){
    val shadowColor = Color.White
    val containerColor = YumaAppTheme.colors.neutral[Colors.TYPE_300.ordinal]
    YumaElevatedCard2(
        shadowColor = shadowColor,
        containerColor = containerColor,
        paddingValues = PaddingValues(bottom = 4.dp,top = 1.dp,start = 1.dp,end = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
       Box {
           Spacer(modifier = Modifier
               .height(36.dp)
               .wrapContentWidth()
           )
           Row(
               modifier = Modifier.height(36.dp),
               verticalAlignment = Alignment.CenterVertically
           ) {
               Image(
                   painter = painterResource(R.drawable.ic_two_wheeler),
                   contentDescription = null,
                   modifier = Modifier
                       .padding(start = 48.dp, end = 4.dp)
               )
               Text(
                   text = "${travelDuration}  ",
                   style = LocalTypography.current.smallBodyMedium.copy(
                       color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
                       fontSize = 12.sp
                   ),
                   modifier = Modifier
               )
               Box(
                   modifier = Modifier
                       .size(4.dp)
                       .clip(RoundedCornerShape(100))
                       .background(Color.Black)
               )
               Text(
                   text = "  $stationDistance",
                   style = LocalTypography.current.smallBodyMedium.copy(
                       color = LocalColors.current.neutral[Colors.TYPE_900.ordinal],
                       fontSize = 12.sp
                   ),
                   modifier = Modifier
                       .padding(end = 8.dp)
               )
           }

       }
    }
}