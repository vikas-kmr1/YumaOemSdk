package com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.components.diy_check_in_screen

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

import com.yumaoem.core.utils.noRippleDebounceClickable
import com.yumaoem.core.utils.time_utils.formatTimeFromSeconds

import com.yumaoem.core_ui.theme.YumaAppTheme
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.core_ui.theme.shapes.LocalAppShapes
import com.yumaoem.core_ui.theme.typography.LocalTypography
import com.yumaoem.feature_home.domain.model.maps.all_station_markers.YumaStationMarker
import com.yumaoem.feature_home.domain.model.maps.all_station_markers.YumaStationStatus
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.ChargingStationState
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.station_info_bottom_sheet_common_components.GoogleMapLogoIcon
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel.LatLong
import com.yumaoem.feature_home.presentation.home_screen.token_booking_flow.check_in_screen.components.InwardCappedDashedDivider



@Composable
fun DiyCheckInScreen(
    station: YumaStationMarker?,
    tokenNumber: String,
    expiryTimeLeft: String,
    modifier: Modifier = Modifier,
    onGetDirectionsClicked: () -> Unit,
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        CheckInScreenHeaderView(
            tokenNumber = tokenNumber,
            expiryTimeLeft =expiryTimeLeft,
        )
        DiyTokenDetailsView(
            station = station,
            onGetDirectionsClicked = onGetDirectionsClicked
        )
    }
}

@Composable
fun DiyTokenDetailsView(
    ycuIconUrl:String = "https://yuma-static.s3.ap-south-1.amazonaws.com/gen5/ic_diy_station_2x.png",
    station: YumaStationMarker?,
    onGetDirectionsClicked: () -> Unit,
) {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(top = 16.dp, bottom = 28.dp)
                    .fillMaxWidth()
                    .padding(
                        start = LocalDimensions.current.dimen20dp,
                        end = LocalDimensions.current.dimen20dp
                    ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                shape = LocalAppShapes.current.textFieldShape,
                colors = CardDefaults.cardColors().copy(
                    containerColor = LocalColors.current.primary[Colors.TYPE_300.ordinal]
                )
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(
                            vertical = LocalDimensions.current.dimen24dp
                        )
                ) {
                    AsyncImage(
                        model = ycuIconUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .width(63.dp)
                            .height(107.dp),
                        contentScale = ContentScale.FillBounds
                    )
                    Spacer(modifier = Modifier.height(LocalDimensions.current.dimen14dp))
                    Text(
                        text = station?.title.orEmpty(),
                        style = LocalTypography.current.bodyLargeSemiBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(LocalDimensions.current.dimen20dp))
                    InwardCappedDashedDivider()
                    Spacer(modifier = Modifier.height(LocalDimensions.current.dimen20dp))

                    val closingTime = if (station?.stationCurrentStatus?.stationState == ChargingStationState.DIY_WITH_TIMING){
                        "Closes ${station.stationCurrentStatus.nextClosingTime?.formatTimeFromSeconds().toString()}"
                    } else {
                        "Open 24x7"
                    }

                    DiyStationDetailsView(
                        modifier = Modifier
                            .noRippleDebounceClickable {
                                onGetDirectionsClicked()
                            }
                            .padding(horizontal = LocalDimensions.current.dimen20dp)
                            .fillMaxWidth(),
                        stationName = station?.stationName.orEmpty(),
                        onDirectionsClicked = onGetDirectionsClicked,
                        stationClosingTime =  closingTime
                    )
                }
            }
        }
    }

}

@Composable
fun CheckInScreenHeaderView(
    tokenNumber: String,
    expiryTimeLeft: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(
                start = LocalDimensions.current.dimen20dp,
                end = LocalDimensions.current.dimen20dp
            )
            .fillMaxWidth()
            .shadow(
                elevation = 1.dp,
                shape = LocalAppShapes.current.textFieldShape,
                clip = false
            )
            .clip(LocalAppShapes.current.textFieldShape)
            .background(color = LocalColors.current.primary[Colors.TYPE_300.ordinal]),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Token: $tokenNumber",
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            style = LocalTypography.current.bodySemiBold,
            textAlign = TextAlign.Left
        )
        ExpiryTimeLeftView(
            expiryTimeLeft = expiryTimeLeft
        )
    }
}

@Composable
fun ExpiryTimeLeftView(
    expiryTimeLeft: String,
    modifier: Modifier = Modifier
){
    Column (
        modifier = modifier
            .shadow(
                elevation = 2.dp,
                shape = LocalAppShapes.current.textFieldShape,
                clip = false
            )
            .clip(LocalAppShapes.current.textFieldShape)
            .background(color = LocalColors.current.primary[Colors.TYPE_300.ordinal])
            .padding(horizontal = 32.dp, vertical = 8.dp),
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Expires In",
            style = LocalTypography.current.smallBodyMedium.copy(
                color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = expiryTimeLeft,
            style = LocalTypography.current.smallBodySemiBold.copy(
                color = LocalColors.current.neutral[Colors.TYPE_900.ordinal]
            )
        )
    }
}

@Composable
fun DiyStationDetailsView(
    modifier: Modifier = Modifier,
    stationName: String,
    stationClosingTime: String = "Open 24x7",
    onDirectionsClicked: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = LocalDimensions.current.dimen8dp)
        ) {
            Text(
                text = stationName,
                style = LocalTypography.current.bodyLargeSemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(LocalDimensions.current.dimen12dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stationClosingTime,
                    style = LocalTypography.current.smallBodyMedium.copy(
                        color = LocalColors.current.neutral[Colors.TYPE_500.ordinal]
                    )
                )
            }
        }
        GoogleMapLogoIcon(onDirectionsClicked = onDirectionsClicked)
    }
}


@Preview
@Composable
fun DiyDIYTokenDetailsViewPreview(){
    YumaAppTheme {
        DiyCheckInScreen(
            station = dummyMarker,
            tokenNumber = "12",
            expiryTimeLeft = "12:04",
            onGetDirectionsClicked = {}
        )
    }
}

val dummyMarker = YumaStationMarker(
    title = "No Commander station",
    stationName = "Yuma Charging",
    stationId = 101,
    location = LatLong(
        latitude = 12.9352,
        longitude = 77.6245
    ),
    numberOfViewsForStation = 5,
    directionsViewCount = 2,
    stationCurrentStatus = YumaStationStatus(
        stationStatusId = 1,
        stationState = ChargingStationState.DIY_ALWAYS_OPEN,
        currentTime = 2343244234,
        nextOpeningTime = 900,  // 9:00 AM
        nextClosingTime = 2100, // 9:00 PM
        distanceFromUser = "1.2 km",
        distanceInMeters = 1200.0,
        travelDuration = "5 mins",
        isAmongNearestStations = true,
        routeData = null
    )
)
