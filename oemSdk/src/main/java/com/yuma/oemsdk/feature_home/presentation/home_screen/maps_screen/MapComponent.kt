package com.yumaoem.feature_home.presentation.home_screen.maps_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.CustomCap
import com.google.android.gms.maps.model.Dot
import com.google.android.gms.maps.model.Gap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.yuma.oemsdk.R
import com.yuma.oemsdk.YumaSdk
import com.yumaoem.core.utils.kmm_flow_util.handle_permissions.PermissionHandlerViewmodel
import com.yumaoem.core.utils.map_style.MapStyle
import com.yumaoem.core.utils.orZero
import com.yumaoem.core_ui.components.permission_denied_dlalog.OpenSettingsDialog
import com.yumaoem.core_ui.theme.color.Colors
import com.yumaoem.core_ui.theme.color.LocalColors
import com.yumaoem.core_ui.utils.collectAsLaunchedEffect
import com.yumaoem.core_ui.utils.snackbar.SnackbarController
import com.yumaoem.core_ui.utils.snackbar.SnackbarEvent
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.dialogs.BookingConfirmationModalBottomSheet
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.dialogs.CannotBookModalBottomSheet
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.dialogs.NoActivePlansModalBottomSheet
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.map_markers.ActiveSelectedYumaStationMarker
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.map_markers.ActiveYumaStationMarker
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.map_markers.InActiveSelectedYumaStationMarker
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.map_markers.InActiveYumaStationMarker
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.components.station_details_carousel.carousel_view.StationsCarouselViewRoot
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel.BookingDialogState
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel.HomeMapScreenEvent
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel.MapScreenUiEvent
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel.MapViewModel
import dev.icerock.moko.permissions.PermissionState
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun MapScreenRoot(
    isHomeTab: Boolean,
    isPostSwap: Boolean = false,
    onTokenBooked: () -> Unit,
    onPurchasePlanClicked: () -> Unit,
    navigateToTagBattery: () -> Unit
) {
    LocationPermissionHandler(
        isHomeTab = isHomeTab,
        isPostSwap = isPostSwap,
        onTokenBooked = onTokenBooked,
        onPurchasePlanClicked = onPurchasePlanClicked,
        navigateToTagBattery = navigateToTagBattery
    )
}

@Composable
fun LocationPermissionHandler(
    isHomeTab: Boolean,
    isPostSwap: Boolean = false,
    onTokenBooked: () -> Unit,
    onPurchasePlanClicked: () -> Unit,
    navigateToTagBattery: () -> Unit,
) {
    val factory = rememberPermissionsControllerFactory()
    val controller = remember(factory) {
        factory.createPermissionsController()
    }
    BindEffect(controller)
    val viewModel = viewModel {
        PermissionHandlerViewmodel(controller)
    }

    val showPermissionDialog = remember { mutableStateOf(false) }
    if (showPermissionDialog.value) {
        OpenSettingsDialog(
            title = "Location",
            onDismissRequest = {
                showPermissionDialog.value = false
            },
            onOpenSettingsClicked = {
                controller.openAppSettings()
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (viewModel.state) {
            PermissionState.Granted -> {
                MapScreenContent(
                    isHomeTab = isHomeTab,
                    isPostSwap = isPostSwap,
                    onTokenBooked = onTokenBooked,
                    onPurchasePlanClicked = onPurchasePlanClicked,
                    navigateToTagBattery = navigateToTagBattery
                )
                showPermissionDialog.value = false
            }

            PermissionState.DeniedAlways -> {
                Text("Permission was permanently declined.")
                showPermissionDialog.value = true
            }

            else -> {
                showPermissionDialog.value = false
                viewModel.provideOrRequestLocationPermission()
            }
        }
    }
}

@Composable
fun MapScreenContent(
    isHomeTab: Boolean,
    isPostSwap: Boolean = false,
    onTokenBooked: () -> Unit,
    onPurchasePlanClicked: () -> Unit,
    navigateToTagBattery: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val viewModel: MapViewModel = viewModel(
            factory = YumaSdk.mapViewModelFactory
        )
        val mapState by viewModel.mapState.collectAsState()

        viewModel.uiEvent.collectAsLaunchedEffect(Unit) { event ->
            when (event) {
                is MapScreenUiEvent.TokenBooked -> {
                    onTokenBooked()
                }

                is MapScreenUiEvent.ShowSnackbar -> {
                    SnackbarController.sendEvent(
                        event = SnackbarEvent(
                            message = event.message,
                        )
                    )
                }

                MapScreenUiEvent.NavigateToTagBattery -> {
                    navigateToTagBattery()
                }

            }
        }

        LaunchedEffect(Unit) {
            viewModel.startMapSession(isPostSwap = isPostSwap)
            viewModel.sendHomeScreenViewedEvent()
        }

        DisposableEffect(
            key1 = Unit,
            effect = {
                onDispose {
                    viewModel.resetLaunchTime()
                }
            }
        )

        val selectedStationIndex =
            mapState.selectedStation?.let { viewModel.getIndexOfStation(station = it) }

        val density = LocalDensity.current
        var carouselHeightPx by remember { mutableStateOf(0) }

        if (isHomeTab) {
            Box(Modifier.fillMaxSize()) {
                MapViewRoot(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            bottom = with(density) { (carouselHeightPx).toDp() }
                        ),
                    viewModel
                )
                if (mapState.carouselStations.isEmpty().not() && isHomeTab) {
                    StationsCarouselViewRoot(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .align(Alignment.BottomCenter)
                            // capture its real height after layout
                            .onGloballyPositioned { coordinates ->
                                carouselHeightPx = coordinates.size.height
                            },
                        stations = mapState.carouselStations,
                        pageCount = mapState.carouselStations.size,
                        selectedStationIndex = selectedStationIndex.orZero(),
                        isBookingInprogress = mapState.bookingInProgress,
                        onStationSelected = {
                            viewModel.onEvent(HomeMapScreenEvent.StationSelectedFromCarousel(it))
                        },
                        onBookBatteryClicked = {
                            //viewModel.onEvent(HomeMapScreenEvent.ShowBookingConfirmationDialog)
                            //onTokenBooked()
                            viewModel.onEvent(event = HomeMapScreenEvent.OnConfirmBookingClicked)
                        },
                        onDirectionsClicked = {
                            viewModel.onEvent(HomeMapScreenEvent.GetDirectionsClicked(it))
                        },
                        onBreakFinished = {
                            viewModel.onEvent(HomeMapScreenEvent.OnBreakFinished)
                        }
                    )
                }
            }
        }


        when (mapState.bookingDialogState) {
            BookingDialogState.BookingInProgress -> {
                BookingConfirmationModalBottomSheet(
                    title = stringResource(R.string.booking_in_progress),
                    isBookingInProgress = true,
                    onDismissRequest = {
                        viewModel.onEvent(event = HomeMapScreenEvent.OnDismissBookingConfirmationDialog)
                    },
                    onBookNowClicked = {
                        viewModel.onEvent(event = HomeMapScreenEvent.OnConfirmBookingClicked)
                    }
                )
            }

            BookingDialogState.Showing -> {
                BookingConfirmationModalBottomSheet(
                    title = stringResource(R.string.confirm_booking),
                    onDismissRequest = {
                        viewModel.onEvent(event = HomeMapScreenEvent.OnDismissBookingConfirmationDialog)
                    },
                    onBookNowClicked = {
                        viewModel.onEvent(event = HomeMapScreenEvent.OnConfirmBookingClicked)
                    }
                )
            }

            BookingDialogState.Hidden -> {}

            BookingDialogState.SubscriptionExpired -> {
                CannotBookModalBottomSheet {
                    viewModel.onEvent(event = HomeMapScreenEvent.OnDismissBookingConfirmationDialog)
                }
            }

            BookingDialogState.PurchasePlanRequired -> {
                NoActivePlansModalBottomSheet(
                    onDismissRequest = {
                        viewModel.onEvent(event = HomeMapScreenEvent.OnDismissBookingConfirmationDialog)
                    },
                    onPurchasePlanClicked = {
                        viewModel.onEvent(event = HomeMapScreenEvent.OnDismissBookingConfirmationDialog)
                        onPurchasePlanClicked()
                    }
                )
            }
        }
    }
}


fun isNextOpeningTimeLessThanOneMinute(nextOpeningTime: Int?): Boolean {
    if (nextOpeningTime == null) return false
    val now = Clock.System.now()
    val local = now.toLocalDateTime(TimeZone.currentSystemDefault())

    val secondsSinceMidnightNow =
        local.hour * 3_600 + local.minute * 60 + local.second

    return (nextOpeningTime - secondsSinceMidnightNow) < 60 && (nextOpeningTime - secondsSinceMidnightNow) > 0
}

@Composable
fun MapViewRoot(
    modifier: Modifier,
    viewModel: MapViewModel,
) {
    Column(modifier = modifier) {
        MapComponent(
            viewModel = viewModel
        )
    }
}

@Composable

fun MapComponent(viewModel: MapViewModel) {
    val currentLocation by viewModel.currentLocation.collectAsState()
    val mapState by viewModel.mapState.collectAsState()

    val stationMarkers = mapState.carouselStations

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(28.6139, 77.2090),
            5f
        )
    }
    var hasCentered by rememberSaveable { mutableStateOf(false) }

    var lastPathHash by rememberSaveable { mutableStateOf<Int?>(null) }
    val selectedPath = mapState.selectedPath.orEmpty()

    val uiSettings = remember {
        MapUiSettings(
            mapToolbarEnabled = false,
            zoomControlsEnabled = false
        )
    }

    val mapProperties = remember {
        MapProperties(
            isMyLocationEnabled = true,
            mapStyleOptions = MapStyleOptions(MapStyle.json2)
        )
    }

    LaunchedEffect(currentLocation, mapState.isMapLoaded) {
        if (
            mapState.isMapLoaded &&
            currentLocation != null &&
            !hasCentered &&
            selectedPath.isEmpty()
        ) {
            cameraPositionState.move(
                update = CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        currentLocation!!.latitude.orZero(),
                        currentLocation!!.longitude.orZero()
                    ),
                    13f
                ),
            )
        }
    }

    LaunchedEffect(selectedPath, mapState.isMapLoaded) {
        if (mapState.isMapLoaded && selectedPath.isNotEmpty()) {
            val boundsBuilder = LatLngBounds.builder()
            selectedPath.forEach { pt ->
                boundsBuilder.include(
                    LatLng(pt.latitude.orZero(), pt.longitude.orZero())
                )
            }
            val bounds = boundsBuilder.build()

            val pathHash = selectedPath.hashCode()
            if (lastPathHash != pathHash) {
                lastPathHash = pathHash
                if (!hasCentered) {
                    val nearbyStations =
                        viewModel.getNearbyStationsAndUserLocation(currentLocation!!)
                    nearbyStations.forEach {
                        boundsBuilder.include(
                            LatLng(
                                it.latitude,
                                it.longitude
                            )
                        )
                    }
                    val innerBounds = boundsBuilder.build()
                    cameraPositionState.move(
                        update = CameraUpdateFactory.newLatLngBounds(
                            innerBounds, 320
                        )
                    )
                    hasCentered = true
                } else {
                    cameraPositionState.animate(
                        update = CameraUpdateFactory.newLatLngBounds(
                            bounds, 160
                        ),
                        durationMs = 550
                    )
                }
            }
        }
    }

    Column(Modifier.fillMaxSize()) {
        var lastClickTime by remember { mutableLongStateOf(0L) }
        Box(Modifier.fillMaxSize()) {
            GoogleMap(
                contentPadding = PaddingValues(
                    start = 0.dp,
                    top = 40.dp,
                    end = 0.dp,
                    bottom = 0.dp
                ),
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                uiSettings = uiSettings,
                properties = mapProperties,
                onMapLoaded = {
                    viewModel.onEvent(HomeMapScreenEvent.OnMapLoaded)
                }
            ) {
                stationMarkers.forEach { station ->
                    MarkerComposable(
                        anchor = Offset(0.1f, 1f),
                        state = MarkerState(
                            LatLng(
                                station.location.latitude,
                                station.location.longitude,
                            )
                        ),
                        onClick = {
                            val currentTime = System.currentTimeMillis()
                            if (currentTime - lastClickTime > 1000) {
                                lastClickTime = currentTime
                                viewModel.onEvent(
                                    HomeMapScreenEvent.StationSelected(station)
                                )
                            }
                            false
                        },
                        keys = arrayOf(
                            station.stationCurrentStatus,
                            mapState.selectedStation?.stationId.orZero()
                        ),
                        zIndex = if (station.stationId == mapState.selectedStation!!.stationId) 1f else 0f
                    ) {
                        when (station.stationCurrentStatus.stationState) {
                            ChargingStationState.OPEN,
                            ChargingStationState.DIY_ALWAYS_OPEN,
                            ChargingStationState.DIY_WITH_TIMING -> {
                                if (station.stationId == mapState.selectedStation!!.stationId) {
                                    ActiveSelectedYumaStationMarker(
                                        stationDistance = station.stationCurrentStatus.distanceFromUser,
                                        travelDuration = station.stationCurrentStatus.travelDuration
                                    )
                                } else {
                                    ActiveYumaStationMarker()
                                }
                            }

                            ChargingStationState.CLOSED, ChargingStationState.BREAK_TIME,
                            ChargingStationState.TEMPORARILY_UNAVAILABLE, ChargingStationState.BATTERY_NOT_AVAILABLE,
                                -> {
                                if (station.stationId == mapState.selectedStation!!.stationId) {
                                    InActiveSelectedYumaStationMarker(
                                        stationDistance = station.stationCurrentStatus.distanceFromUser,
                                        travelDuration = station.stationCurrentStatus.travelDuration
                                    )
                                } else {
                                    InActiveYumaStationMarker()
                                }
                            }

                            ChargingStationState.NOT_AVAILABLE -> {
                                ActiveYumaStationMarker()
                            }
                        }
                    }
                }

                mapState.selectedPath?.takeUnless { it.isEmpty() }?.let { path ->
                    val lineCap =
                        CustomCap(BitmapDescriptorFactory.fromResource(R.drawable.polyline_cap))
                    Polyline(
                        points = path.map {
                            LatLng(it.latitude.orZero(), it.longitude.orZero())
                        },
                        color = LocalColors.current.neutral[Colors.TYPE_800.ordinal],
                        width = 7f,
                        startCap = lineCap,
                        endCap = lineCap
                    )

                    mapState.currentRouteStation?.let { station ->
                        val pathEnd = path.lastOrNull()
                        if (pathEnd != null) {
                            val pathEndLatLng =
                                LatLng(pathEnd.latitude.orZero(), pathEnd.longitude.orZero())
                            val stationLatLng = LatLng(
                                station.location.latitude.orZero(),
                                station.location.longitude.orZero()
                            )

                            // Optional: Skip drawing if points are nearly same
                            val shouldDrawDotted = pathEndLatLng != stationLatLng

                            if (shouldDrawDotted) {
                                Polyline(
                                    points = listOf(pathEndLatLng, stationLatLng),
                                    color = LocalColors.current.neutral[Colors.TYPE_500.ordinal],
                                    width = 10f,
                                    pattern = listOf(Dot(), Gap(20f)),
                                    clickable = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}