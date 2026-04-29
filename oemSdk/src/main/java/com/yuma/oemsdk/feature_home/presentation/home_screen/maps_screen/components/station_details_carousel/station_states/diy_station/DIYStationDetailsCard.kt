package com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.diy_station

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yuma.oemsdk.R
import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.shapes.LocalAppShapes
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.station_info_bottom_sheet_common_components.GoogleMapLogoIcon


@Composable
fun DIYChargingStationContentCard(
    stationName: String,
    title:String,
    ycuIcon: String = "https://yuma-static.s3.ap-south-1.amazonaws.com/gen5/ic_diy_station+(1).png",
    onDirectionsClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
            .clip(LocalAppShapes.current.chipShape)
            .background(
                color = LocalColors.current.primary[Colors.TYPE_300.ordinal]
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Row() {
                        AsyncImage(
                            model = ycuIcon,
                            contentDescription = "ycu icon",
                            modifier = Modifier
                                .height(55.dp)
                                .width(32.dp),
                            contentScale = ContentScale.FillBounds
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        OpenStationStateChip(
                            text = stringResource(R.string.open_24_7),
                        )
                        Text(
                            text = title,
                            style = LocalTypography.current.smallBody.copy(
                                color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
                            )
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(LocalDimensions.current.dimen6dp))
            GoogleMapLogoIcon(
                onDirectionsClicked = onDirectionsClicked
            )
        }
    }
}


@Composable
fun OpenStationStateChip(
    modifier: Modifier = Modifier,
    text: String = stringResource(R .string.open)
) {
    Box(
        modifier = modifier
            .clip(LocalAppShapes.current.chipShape)
            .padding(
                top = LocalDimensions.current.dimen2dp,
                bottom = LocalDimensions.current.dimen2dp
            )
    ) {
        Text(
            text = text,
            style = LocalTypography.current.smallBodySemiBold.copy(
                color = LocalColors.current.green[Colors.TYPE_500.ordinal]
            )
        )
    }
}


@Preview
@Composable
fun DIYChargingStationContentCardPreview() {
    YumaAppTheme {
        DIYChargingStationContentCard(
            stationName = "Station Name",
            title = "No Commander Station",
            onDirectionsClicked = {}
        )
    }
}


