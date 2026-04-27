package com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.carousel_view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.yuma.oemsdk.R
import com.yumaoem.core.utils.orZero
import com.yumaoem.core.utils.time_utils.formatTimeFromSeconds
import com.yumaoem.core_ui.components.buttons.YumaPrimaryButton
import com.yumaoem.core_ui.theme.dimension.LocalDimensions
import com.yumaoem.feature_home.domain.model.maps.all_station_markers.YumaStationMarker
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.ChargingStationState
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.details_loading.StationStatusLoading
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.battries_unavailable_station.BatteriesUnavailableStationContentCard
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.closed_station.ClosedStationContentCard
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.diy_station.DIYChargingStationContentCard
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.diy_station.DiyStationWithTimingCard
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.on_break_station.StationOnBreakContentCard
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.operational_station.OperationalChargingStationContentCard
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.station_info_bottom_sheet_common_components.YumaStationBottomCardLayout
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.station_states.station_on_break_with_timer.StationOnBreakWithTimerCard
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.isNextOpeningTimeLessThanOneMinute


@Composable
fun StationsCarouselViewRoot(
    pageCount: Int,
    selectedStationIndex: Int,
    stations: List<YumaStationMarker> = emptyList(),
    modifier: Modifier = Modifier,
    pagerState: PagerState = rememberPagerState(
        initialPage = selectedStationIndex,
        pageCount = { pageCount }
    ),
    isBookingInprogress: Boolean,
    onDirectionsClicked: (YumaStationMarker) -> Unit,
    onStationSelected: (YumaStationMarker) -> Unit,
    onBookBatteryClicked: () -> Unit,
    onBreakFinished: () -> Unit
) {
    if(stations.isNotEmpty()){
        YumaStationBottomCardLayout(
            content = {
                CarouselViewContent(
                    pagerState = pagerState,
                    stations = stations,
                    initialPage = selectedStationIndex,
                    onStationSelected = {
                        onStationSelected(it)
                    },
                    onDirectionsClicked = {
                        onDirectionsClicked(
                            stations[selectedStationIndex]
                        )
                    },
                    onBreakFinished = onBreakFinished
                )
            },
            actionButton = {
                val selectedStation = stations[selectedStationIndex]
                val isBookBatteryButtonEnabled =
                            selectedStation.stationCurrentStatus.stationState == ChargingStationState.OPEN ||
                            selectedStation.stationCurrentStatus.stationState == ChargingStationState.DIY_WITH_TIMING ||
                            selectedStation.stationCurrentStatus.stationState == ChargingStationState.DIY_ALWAYS_OPEN
                YumaPrimaryButton(
                    isLoading = isBookingInprogress,
                    enabled = isBookBatteryButtonEnabled,
                    buttonText = stringResource(R.string.book_battery),
                    trailingIcon = {
                        Image(
                            modifier = Modifier.padding(start = LocalDimensions.current.dimen8dp),
                            painter = painterResource(R.drawable.arrow_right),
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.padding(horizontal = LocalDimensions.current.dimen20dp)
                ){
                    onBookBatteryClicked()
                }
            },
            modifier = modifier
        )
    }
}

@Composable
private fun CarouselViewContent(
    pagerState: PagerState,
    stations: List<YumaStationMarker>,
    initialPage: Int,
    onDirectionsClicked: () -> Unit,
    onStationSelected: (YumaStationMarker) -> Unit,
    onBreakFinished: () -> Unit
) {

    // Prevent callback during initial scroll
    var hasScrolledManually by remember { mutableStateOf(false) }

    LaunchedEffect(initialPage) {
        pagerState.scrollToPage(initialPage)
    }

    // Listen to page changes and trigger only if it's a manual scroll
    LaunchedEffect(pagerState.currentPage) {
        if (hasScrolledManually) {
            onStationSelected(stations[pagerState.currentPage])
        } else {
            hasScrolledManually = true
        }
    }

    HorizontalPager(
        state = pagerState,
        contentPadding = PaddingValues(horizontal = LocalDimensions.current.dimen20dp),
        key = { pageIndex->
            stations[pageIndex].stationId
        }
    ) { page ->
        val currentStation = stations[page]
        if (currentStation.stationCurrentStatus.isAmongNearestStations.not()){
            StationStatusLoading()
        }else{
            when(currentStation.stationCurrentStatus.stationState){
                ChargingStationState.OPEN -> {
                    OperationalChargingStationContentCard(
                        stationName = currentStation.stationName,
                        stationClosingTime = currentStation.stationCurrentStatus.nextClosingTime.orZero().formatTimeFromSeconds(),
                        onDirectionsClicked = onDirectionsClicked
                    )
                }
                ChargingStationState.CLOSED -> {
                    ClosedStationContentCard(
                        stationName = currentStation.stationName,
                        stationOpeningTime = currentStation.stationCurrentStatus
                            .nextOpeningTime.orZero().formatTimeFromSeconds(),
                        onDirectionsClicked = onDirectionsClicked
                    )
                }
                ChargingStationState.BREAK_TIME -> {
                    if (isNextOpeningTimeLessThanOneMinute(
                            currentStation.stationCurrentStatus.nextOpeningTime
                        )){
                        StationOnBreakWithTimerCard(
                            stationName = currentStation.stationName,
                            stationOpeningTimeStamp = currentStation.stationCurrentStatus.nextOpeningTime.orZero(),
                            onDirectionsClicked = onDirectionsClicked,
                            onBreakFinished = onBreakFinished
                        )
                    }else{
                        StationOnBreakContentCard(
                            stationName = currentStation.stationName,
                            stationOpeningTime = currentStation.stationCurrentStatus.nextOpeningTime
                                .orZero().formatTimeFromSeconds(),
                            onDirectionsClicked = onDirectionsClicked
                        )
                    }
                }
                ChargingStationState.BATTERY_NOT_AVAILABLE -> {
                    BatteriesUnavailableStationContentCard(
                        stationName = currentStation.stationName,
                        onDirectionsClicked = onDirectionsClicked
                    )
                }

                ChargingStationState.NOT_AVAILABLE -> {
                    StationStatusLoading()
                }

                ChargingStationState.TEMPORARILY_UNAVAILABLE -> {
                    BatteriesUnavailableStationContentCard(
                        stationName = currentStation.stationName,
                        onDirectionsClicked = onDirectionsClicked,
                        text = "Temporarily Closed"
                    )
                }

                ChargingStationState.DIY_ALWAYS_OPEN -> {
                    DIYChargingStationContentCard(
                        stationName = currentStation.stationName,
                        title = currentStation.title.orEmpty(),
                        onDirectionsClicked = onDirectionsClicked
                    )
                }

                ChargingStationState.DIY_WITH_TIMING -> {
                    DiyStationWithTimingCard(
                        stationName = currentStation.stationName,
                        stationClosingTime =  currentStation.stationCurrentStatus.nextClosingTime.orZero().formatTimeFromSeconds(),
                        onDirectionsClicked = onDirectionsClicked,
                        title = currentStation.title.orEmpty()
                    )
                }
            }
        }

    }
}
