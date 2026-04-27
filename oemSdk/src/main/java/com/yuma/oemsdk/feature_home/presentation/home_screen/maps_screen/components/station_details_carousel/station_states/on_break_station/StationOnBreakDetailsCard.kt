package com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.on_break_station

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yuma.oemsdk.R
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.shapes.LocalAppShapes
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.closed_station.ClosedStationStateChip
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.station_info_bottom_sheet_common_components.GoogleMapLogoIcon

@Composable
fun StationOnBreakContentCard(
    stationName:String,
    stationOpeningTime:String,
    onDirectionsClicked : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clip(LocalAppShapes.current.chipShape)
            .background(
                color = LocalColors.current.red[Colors.TYPE_300.ordinal]
            )
            .padding(all = LocalDimensions.current.dimen20dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = stationName,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = LocalTypography.current.bodyLargeSemiBold
                )
                Spacer(modifier = Modifier.height(LocalDimensions.current.dimen12dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ClosedStationStateChip("Break time")
                    Text(
                        text = stringResource(R.string.char_i),
                        style = LocalTypography.current.body.copy(
                            color = LocalColors.current.neutral[Colors.TYPE_300.ordinal]
                        ),
                        modifier = Modifier.padding(horizontal = LocalDimensions.current.dimen6dp)
                    )
                    Text(
                        text ="Opens $stationOpeningTime",
                        style = LocalTypography.current.smallBody.copy(
                            color = LocalColors.current.neutral[Colors.TYPE_500.ordinal]
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.width(LocalDimensions.current.dimen6dp))
            GoogleMapLogoIcon(
                onDirectionsClicked = onDirectionsClicked
            )
        }
    }
}