package com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.yumaoem.core.model.auth.User
import com.yumaoem.core.utils.currentTimeMillis
import com.yumaoem.core.utils.kmm_flow_util.CommonStateFlow
import com.yumaoem.core.utils.kmm_flow_util.toCommonStateFlow
import com.yumaoem.core.utils.map_style.calculateDistanceInMeters
import com.yumaoem.core.utils.orZero
import com.yumaoem.core_network.impl.util.collect
import com.yumaoem.corepreference.api.YumaPrefUtilApi
import com.yumaoem.feature_home.common.notification.ServiceLauncher
import com.yumaoem.feature_home.common.toDTO
import com.yumaoem.feature_home.common.util.openMapsDirections
import com.yumaoem.feature_home.data.dto.book_token.request.BookTokenRequest
import com.yumaoem.feature_home.data.dto.maps_screen.all_stations.request.AllStationsRequest
import com.yumaoem.feature_home.data.dto.station_directions.request.RouteInfoRequest
import com.yumaoem.feature_home.data.dto.station_operation_status.request.StationOperationStatusRequest
import com.yumaoem.feature_home.data.dto.station_operation_status.response.isOperational
import com.yumaoem.feature_home.domain.model.maps.all_station_markers.YumaStationMarker
import com.yumaoem.feature_home.domain.model.maps.stationRoute.RouteInfo
import com.yumaoem.feature_home.domain.model.token_flow.book_token.BookedTokenDetails
import com.yumaoem.feature_home.domain.usecase.get_battery_details.GetBatteryDetailsUseCase
import com.yumaoem.feature_home.domain.usecase.maps.all_station_markers.GetAllStationsUseCase
import com.yumaoem.feature_home.domain.usecase.maps.route_info.GetRouteInfoUseCase
import com.yumaoem.feature_home.domain.usecase.maps.station_operation_status.GetStationOperationStatusUseCase
import com.yumaoem.feature_home.domain.usecase.token_booking.book_token.BookTokenUseCase
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.user_current_location_provider.LocationProvider
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel.MapScreenUiEvent.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.math.round

/**
 * Shared ViewModel for Android, Common, and iOS targets.
 * Changes here will propagate to all platforms and may
 * introduce breaking issues if not handled carefully.
 */

class MapViewModel(
    private val locationProvider: LocationProvider,
    private val bookTokenUseCase: BookTokenUseCase,
    private val getAllStationsUseCase: GetAllStationsUseCase,
    private val getRouteInfoUseCase: GetRouteInfoUseCase,
    private val stationOperationStatusUseCase: GetStationOperationStatusUseCase,
  //  private val loggerApi: LoggerApi,
    private val prefUtilApi: YumaPrefUtilApi,
    private val serviceLauncher: ServiceLauncher,
   // private val analyticsApi: AnalyticsApi,
    private val getBatteryDetailsUseCase: GetBatteryDetailsUseCase,
) : ViewModel() {

    private val _mapState = MutableStateFlow(MapState())
    val mapState: CommonStateFlow<MapState> = _mapState.toCommonStateFlow()

    private val _uiEvent = MutableSharedFlow<MapScreenUiEvent>()
    val uiEvent: SharedFlow<MapScreenUiEvent> = _uiEvent

    val currentLocation: CommonStateFlow<LatLong?> = locationProvider
        .currentLocation
        .toCommonStateFlow()

    private var cachedUser: User? = null
    private var isFirstLocationHandled = false

    fun onEvent(event: HomeMapScreenEvent) {
        when (event) {
            HomeMapScreenEvent.OnMapLoaded -> {
                _mapState.update { it.copy(isMapLoaded = true) }
            }

            is HomeMapScreenEvent.StationSelectedFromCarousel -> {
                if (event.station.stationId == mapState.value.selectedStation?.stationId) { return }
                onMarkerSelected(event.station)
            }

            is HomeMapScreenEvent.StationSelected -> {
                if (event.station.stationId == mapState.value.selectedStation?.stationId){
                    val station = event.station
                    val directionsViewCount = station.directionsViewCount
                    val updatedMarker = station.copy(
                        directionsViewCount = directionsViewCount + 1
                    )
                    _mapState.value = _mapState.value.copy(
                        carouselStations = _mapState.value.carouselStations.map {
                            if (it.stationId == updatedMarker.stationId) updatedMarker else it
                        }
                    )
                    sendDirectionsViewEvent(
                        marker = updatedMarker,
                        ctaUsed = "map"
                    )
                    openMapsDirections(
                        latitude = event.station.location.latitude,
                        longitude = event.station.location.longitude
                    )

                }else{
                    onMarkerSelected(event.station)
                }

            }

            HomeMapScreenEvent.OnConfirmBookingClicked -> {
                bookTokenAtStation()
            }

            HomeMapScreenEvent.OnDismissBookingConfirmationDialog -> {
                _mapState.update { it.copy(bookingDialogState = BookingDialogState.Hidden) }
            }

            HomeMapScreenEvent.ShowBookingConfirmationDialog -> {
                _mapState.update { it.copy(bookingDialogState = BookingDialogState.Showing) }
            }

            is HomeMapScreenEvent.GetDirectionsClicked -> {
                val station = event.station
                val directionsViewCount = station.directionsViewCount
                val updatedMarker = station.copy(
                    directionsViewCount = directionsViewCount + 1
                )
                _mapState.value = _mapState.value.copy(
                    carouselStations = _mapState.value.carouselStations.map {
                        if (it.stationId == updatedMarker.stationId) updatedMarker else it
                    }
                )
                sendDirectionsViewEvent(
                    marker = updatedMarker,
                    ctaUsed = "station_card"
                )
                openMapsDirections(
                    latitude = event.station.location.latitude,
                    longitude = event.station.location.longitude
                )
            }

            HomeMapScreenEvent.OnBreakFinished -> {
                viewModelScope.launch {
                    currentLocation.value?.let { getAllNearbyStations(it) }
                }
            }
        }
    }

    private fun bookTokenAtStation() {
        serviceLauncher.startDummyNotification()
        viewModelScope.launch {
            val userDetails: User? = mapState.value.userDetails
            userDetails.let { it ->
                if (it == null) {
                    serviceLauncher.stopForeGroundNotification()
                    _uiEvent.emit(MapScreenUiEvent.ShowSnackbar("User details not found"))
                    return@launch
                }else{
                    val distance = mapState.value.selectedStation?.stationCurrentStatus?.distanceInMeters
                    bookTokenUseCase.invoke(
                        bookTokenRequest = BookTokenRequest(
                            chargingStationId = mapState.value.selectedStation?.stationId.orZero(),
                            latitude = currentLocation.value?.latitude.orZero(),
                            longitude = currentLocation.value?.longitude.orZero(),
                            clientId = it.clientId,
                            clientUserId = it.clientUserId,
                            clientCityId = it.clientCityId,
                            clientVehicleId = it.clientVehicleId,
                            clientVehicleQrCode = it.clientVehicleQrCode,
                            vehicleItemGroupId = it.clientVehicleGroupId,
                            userId = it.userId.toInt(),
                            tokenStatusId = 1,
                            isDiyToken = false,
                            distanceFromChargingStation = distance?.toInt()
                        )
                    ).collect(
                        onLoading = {
                            _mapState.update { it.copy(bookingInProgress = true) }
                        },
                        onSuccess = { data ->
                            _mapState.update {
                                it.copy(bookingDialogState = BookingDialogState.Hidden)
                            }
                            val bookedTokenDetails: BookedTokenDetails = data

                            var bookedTokenDetailsDTO = bookedTokenDetails.toDTO()

                            bookedTokenDetailsDTO = bookedTokenDetailsDTO.copy(
                                bookingStation = mapState.value.selectedStation!!.toDTO()
                            )

                            prefUtilApi.saveBookedTokenDetails(
                                bookedTokenDetails = bookedTokenDetailsDTO
                            )
                            sendBookTokenEvent(
                                bookedTokenDetails.tokenNumber,
                                bookedTokenDetails.tokenID,
                                isDiy = bookedTokenDetails.isDiyToken
                            )
                            serviceLauncher.sendForeGroundNotification(
                                data.tokenExpiryTimeStamp.toString(),
                                mapState.value.selectedStation!!.stationName
                            )
                            _mapState.update { it.copy(bookingInProgress = false) }
                            _uiEvent.emit(MapScreenUiEvent.TokenBooked)
                        },
                        onError = { errorMessage, _ ->
                            serviceLauncher.stopForeGroundNotification()
                            _mapState.update { it.copy(bookingDialogState = BookingDialogState.Hidden) }

                            when (val error = mapBookingError(errorMessage)) {
                                is BookingError.SubscriptionExpired -> {
                                    _mapState.update { it.copy(bookingDialogState = BookingDialogState.SubscriptionExpired) }
                                }

                                is BookingError.PurchasePlanRequired -> {
                                    _mapState.update { it.copy(bookingDialogState = BookingDialogState.PurchasePlanRequired) }
                                }

                                is BookingError.Unknown -> {
                                    _uiEvent.emit(ShowSnackbar(error.message))
                                }

                                is BookingError.TagBattery -> {
                                    _uiEvent.emit(NavigateToTagBattery)
                                }
                            }

                            _mapState.update { it.copy(bookingInProgress = false) }

                            sendTokenBookingFailedEvent(failureReason = errorMessage)
                        }

                    )
                }

            }
        }
    }

    init {
        onMapViewModelInit()
    }

    fun resetLaunchTime(){
        _mapState.update { it.copy(launchTimeStamp = currentTimeMillis()) }
    }

    private fun onMapViewModelInit() {
        viewModelScope.launch {
            //loggerApi.logDWithTag("MapViewModel","onMapViewModelInit")
            cachedUser = prefUtilApi.getUserData()
            _mapState.update {
                it.copy(userDetails = cachedUser)
            }
            loadBatteryDetails()
            locationProvider.startLocationUpdates()

            currentLocation.collectLatest { location ->
                _mapState.update { current ->
                    //loggerApi.logDWithTag("MapViewModel","location updated init")
                    current.copy(
                        userLocation = location,
                        launchTimeStamp = currentTimeMillis()
                    )
                }
            }
        }
    }

    /**
     * Called once from the composable via LaunchedEffect(Unit).
     * Controls the first station fetch and whether the 2-station route
     * optimization is skipped (post-swap return).
     * isFirstLocationHandled is set inside getNearestStations() only on success,
     */
    fun startMapSession(isPostSwap: Boolean = false) {
        //loggerApi.logDWithTag("MapViewModel", "startMapSession with isPostSwap=$isPostSwap")
        if (isFirstLocationHandled) return
        viewModelScope.launch {
            if (cachedUser == null) {
                cachedUser = prefUtilApi.getUserData()
            }
            val location = _mapState.value.userLocation ?: currentLocation.first { it?.latitude != null } ?: return@launch
            getAllNearbyStations(location, skipOptimization = isPostSwap)
        }
    }


    fun getNearbyStationsAndUserLocation(currentLocation: LatLong): List<LatLong> {
        val stationLocations = _mapState.value.carouselStations
            .take(3)
            .map { it.location }

        return stationLocations + currentLocation
    }

    private suspend fun getAllNearbyStations(location: LatLong, skipOptimization: Boolean = false) {
        getAllStationsUseCase.invoke(
            nearbyStationsRequest = AllStationsRequest(
                latitude = location.latitude,
                longitude = location.longitude,
                clientId = cachedUser?.clientId.orZero(),
                clientCityId = cachedUser?.clientCityId.orZero(),
                userId = cachedUser?.clientUserId.orZero(),
                clientVehicleId = cachedUser?.clientVehicleId.orZero()
            )
        ).collect(
            onLoading = {},
            onSuccess = { data ->
                if (data.isEmpty().not()){
                    getNearestStations(data, location, skipOptimization)
                }else{
                    _uiEvent.emit(MapScreenUiEvent.ShowSnackbar("No nearby stations found"))
                }

            },
            onError = { errorMessage, _ ->
                _uiEvent.emit(MapScreenUiEvent.ShowSnackbar(errorMessage))
            }
        )
    }

    private fun getNearestStations(
        stationsList: List<YumaStationMarker>,
        currentLocation: LatLong,
        skipOptimization: Boolean = false
    ) {
        //loggerApi.logDWithTag("MapViewModel", "getNearestStations")
        if (stationsList.isEmpty()) return

        viewModelScope.launch(Dispatchers.Default) {
            val sortedStations = stationsList.map { station ->
                val distance = calculateDistanceInMeters(
                    startLat = currentLocation.latitude,
                    startLng = currentLocation.longitude,
                    endLat = station.location.latitude,
                    endLng = station.location.longitude
                )

                station.copy(
                    stationCurrentStatus = station.stationCurrentStatus.copy(
                        distanceInMeters = distance
                    )
                )
            }.sortedBy { it.stationCurrentStatus.distanceInMeters }
            isFirstLocationHandled = true

            var firstOpenStation = sortedStations.first()
            _mapState.update {
                it.copy(
                    carouselStations = sortedStations,
                    selectedStation = firstOpenStation
                )
            }

            /**
             *  1. are all first three stations closed? [select the nearest open station]
             *  2. two stations are closed and one is on break [select the station on break]
             *  3. all stations are open [select the nearest open station]
             *  4. only one station is open [select the nearest open station]
             */
            val top5Stations = sortedStations.take(5)

            val hasNoOpenStation = top5Stations.all {
                !it.stationCurrentStatus.stationState.isOperational()
            }

            try {
                if (hasNoOpenStation) {
                    //loggerApi.logDWithTag("MapViewModel", "no open stations in top 5")
                    // no open stations in top 5 — find the first open anywhere
                    firstOpenStation = sortedStations.first { it.stationCurrentStatus.stationState.isOperational() }
                    onMarkerSelected(firstOpenStation)
                } else {
                    // at least one open in top 5
                    firstOpenStation = sortedStations.first { it.stationCurrentStatus.stationState.isOperational() }
                    //loggerApi.logDWithTag("MapViewModel", "at least one open station in top 5 - $firstOpenStation")

                    val openStationsInTop5 = top5Stations
                        .filter { it.stationCurrentStatus.stationState.isOperational() }
                        .distinctBy { it.stationId }
                        .take(2)

                    if (openStationsInTop5.size == 2 && !skipOptimization) {
                        //loggerApi.logDWithTag("MapViewModel", "two open stations in top 5 - with skipOptimization = $skipOptimization")
                        getTopTwoStationsRouteData(
                            origin = currentLocation,
                            topTwoStations = openStationsInTop5
                        )
                    } else {
                        //loggerApi.logDWithTag("MapViewModel", "one open station in top 5")
                        onMarkerSelected(firstOpenStation)
                    }
                }
            } catch (e: Exception) {
                onMarkerSelected(sortedStations.first())
            }
        }
    }


    fun getIndexOfStation(station: YumaStationMarker): Int {
        val res = _mapState.value.carouselStations.indexOf(station)
        return if (res == -1) 0 else res
    }

    private fun onMarkerSelected(marker: YumaStationMarker) {
        val currentState = mapState.value
        val origin = mapState.value.userLocation
        val numberOfStationsSelected = mapState.value.numberOfStationsSelected
        val updatedMarker = marker.copy(
            numberOfViewsForStation = marker.numberOfViewsForStation + 1
        )
        _mapState.value = _mapState.value.copy(
            selectedStation = updatedMarker,
            numberOfStationsSelected = numberOfStationsSelected+1,
            carouselStations = currentState.carouselStations.map {
                if (it.stationId == updatedMarker.stationId) updatedMarker else it
            }
        )

        if (marker.stationCurrentStatus.routeData != null) {
            updateMapWithExistingRoute(marker)
        } else {
            if (origin!=null){
                fetchAndUpdateRouteData(origin, marker)
            }
        }

        if (marker.stationCurrentStatus.isAmongNearestStations.not()) {
            viewModelScope.launch {
                getStationOperationStatus(marker.stationId,marker)
            }
        } else{
            sendStationViewEvent(marker)
        }
    }

    private fun updateMapWithExistingRoute(marker: YumaStationMarker) {
        _mapState.update {
            it.copy(
                selectedPath = marker.stationCurrentStatus.routeData?.polylinePoints,
                currentRouteStation = marker
            )
        }
    }

    private fun fetchAndUpdateRouteData(origin: LatLong, marker: YumaStationMarker) {
        getRouteData(origin, marker) { route ->
            val currentStation = mapState.value.selectedStation
            if (currentStation==null) return@getRouteData
            _mapState.update { currentMapState ->
                val distance = route.distanceInMeters
                val distanceDisplay = formatDistance(distance)
                val travelDuration = formatTime(route.durationInSeconds)
                val updatedMarker = currentStation.copy(
                    stationCurrentStatus = currentStation.stationCurrentStatus.copy(
                        distanceFromUser = distanceDisplay,
                        distanceInMeters = distance,
                        travelDuration = travelDuration,
                        routeData = route,
                    )
                )
                currentMapState.copy(
                    currentRouteStation = updatedMarker,
                    selectedStation = updatedMarker,
                    selectedPath = route.polylinePoints,
                    carouselStations = updateCarouselStations(
                        currentMapState.carouselStations,
                        updatedMarker
                    )
                )
            }
        }
    }

    private fun formatDistance(distance: Double): String {
        return if (distance < 100) {
            "${distance.toInt()} m"
        } else {
            val km = round(distance / 100.0) / 10.0
            "$km km"
        }
    }

    private fun formatTime(timeInSeconds: Int): String {
        return "${secondsToMinutes(timeInSeconds)} min"
    }

    private fun secondsToMinutes(seconds: Int): Int {
        return seconds / 60
    }


    private fun updateCarouselStations(
        carouselStations: List<YumaStationMarker>,
        updatedMarker: YumaStationMarker
    ): List<YumaStationMarker> {
        return carouselStations.map { station ->
            if (station.stationId == updatedMarker.stationId) {
                println("updated marker =${updatedMarker}")
                station.copy(
                    stationCurrentStatus = updatedMarker.stationCurrentStatus
                )
            } else {
                station
            }
        }
    }

    private fun getRouteData(
        origin: LatLong?,
        destination: YumaStationMarker,
        onGettingRouteInfo: (RouteInfo) -> Unit,
    ) {
        viewModelScope.launch {
            getRouteInfoUseCase.invoke(
                RouteInfoRequest(
                    origin = origin!!,
                    destination = destination.location
                )
            ).collect(
                onLoading = {},
                onSuccess = { route: RouteInfo? ->
                    route?.let { onGettingRouteInfo(it) }
                    sendGoogleMapApiEvent()
                },
                onError = { _, _ ->
                    _uiEvent.emit(MapScreenUiEvent.ShowSnackbar("Failed to load route"))
                }
            )
        }
    }

    private suspend fun getRouteDataSuspending(
        origin: LatLong,
        destination: YumaStationMarker
    ): RouteInfo = suspendCancellableCoroutine { cont ->
        getRouteData(origin, destination) { route ->
            cont.resume(route)
        }
    }

    private fun getTopTwoStationsRouteData(
        origin: LatLong,
        topTwoStations: List<YumaStationMarker>
    ) {
        viewModelScope.launch {
            try {
                val routes = listOf(
                    async { getRouteDataSuspending(origin, topTwoStations[0]) },
                    async { getRouteDataSuspending(origin, topTwoStations[1]) }
                ).awaitAll()

                val routeResults = routes.mapIndexed { index, route ->
                    val station = topTwoStations[index]
                    val distance = route.distanceInMeters
                    val distanceDisplay = formatDistance(distance)
                    val timeInMinutes = formatTime(route.durationInSeconds)
                    val updatedMarker = station.copy(
                        stationCurrentStatus = station.stationCurrentStatus.copy(
                            distanceFromUser = distanceDisplay,
                            distanceInMeters = distance,
                            routeData = route,
                            travelDuration = timeInMinutes
                        )
                    )
                    _mapState.update { currentMapState ->
                        updateCarouselStations(
                            currentMapState.carouselStations,
                            updatedMarker
                        )
                        currentMapState.copy(
                            carouselStations = updateCarouselStations(
                                currentMapState.carouselStations,
                                updatedMarker
                            )
                        )
                    }
                    // Return index + distance for comparison
                    Pair(index, distance)
                }

                // Compare and select closer station
                val (closerIndex, _) = routeResults.minByOrNull { it.second }!!
                val closerStation = topTwoStations[closerIndex]

                _mapState.update {
                    it.copy(selectedStation = closerStation)
                }
                val closerStationWithRoute = _mapState.value.carouselStations
                    .firstOrNull { it.stationId == topTwoStations[closerIndex].stationId }
                    ?: topTwoStations[closerIndex]
                onMarkerSelected(closerStationWithRoute)
            } catch (e: Exception) {
                e.printStackTrace()
                _uiEvent.emit(MapScreenUiEvent.ShowSnackbar("Failed to load route for one or more stations"))
            }
        }
    }


    private suspend fun getStationOperationStatus(csId: Int, marker1: YumaStationMarker) {
        val userDetails = mapState.value.userDetails
        stationOperationStatusUseCase.invoke(
            stationOperationStatusRequest = StationOperationStatusRequest(
                csId = csId,
                clientVehicleId = userDetails?.clientVehicleId.orZero(),
                clientCityId = userDetails?.clientCityId.orZero()
            )
        ).collect(
            onLoading = {},
            onSuccess = { currentStationStatus->
                // Update map state
                val updatedStations = _mapState.value.carouselStations.map { marker ->
                    if (marker.stationId == csId) {
                        marker.copy(stationCurrentStatus = currentStationStatus)
                    } else marker
                }
                _mapState.update {
                    it.copy(
                        carouselStations = updatedStations,
                        selectedStation = updatedStations.find { it.stationId == csId }
                    )
                }
                sendStationViewEvent(marker1)
            },
            onError = { _, _ ->
                _uiEvent.emit(MapScreenUiEvent.ShowSnackbar("Failed to get station status"))
            }
        )
    }

    private fun sendDirectionsViewEvent(
        marker: YumaStationMarker,
        ctaUsed:String
    ){
        viewModelScope.launch {
            val currentStation = mapState.value.selectedStation
            val currentUser = prefUtilApi.getUserData()
//            analyticsApi.postEvent(
//                event = "directions_viewed",
//                values = mapOf(
//                    "user_id" to currentUser?.userId.orEmpty(),
//                    "name" to "${currentUser?.firstName.orEmpty()} ${currentUser?.surname.orEmpty()}",
//                    "mobile_number" to currentUser?.phone.orEmpty(),
//                    "distance" to (currentStation?.stationCurrentStatus?.distanceInMeters?.toString().orEmpty()),
//                    "direction_view_count" to marker.directionsViewCount.toString(),
//                    "cta_used" to ctaUsed,
//                    "station_id" to (currentStation?.stationId?.toString().orEmpty()),
//                    "bike_qr_number" to currentUser?.clientVehicleQrCode.orEmpty()
//                )
//            )
        }
    }

    private fun sendStationViewEvent(
        marker: YumaStationMarker
    ) {
        viewModelScope.launch {
            delay(500)
            val currentStation = mapState.value.selectedStation
            val currentUser = prefUtilApi.getUserData()
//            analyticsApi.postEvent(
//                event = "station_viewed",
//                values = mapOf(
//                    "user_id" to currentUser?.userId.orEmpty(),
//                    "name" to "${currentUser?.firstName.orEmpty()} ${currentUser?.surname.orEmpty()}",
//                    "mobile_number" to currentUser?.phone.orEmpty(),
//                    "distance" to (currentStation?.stationCurrentStatus?.distanceInMeters?.toString().orEmpty()),
//                    "station_id" to (currentStation?.stationId?.toString().orEmpty()),
//                    "station_name" to currentStation?.stationName.orEmpty(),
//                    "station_status" to currentStation?.stationCurrentStatus?.stationState?.name.toString(),
//                    "view_count" to marker.numberOfViewsForStation.toString(),
//                    "battery_details" to mapState.value.batteryDetails.toString(),
//                    "bike_qr_number" to currentUser?.clientVehicleQrCode.orEmpty()
//                )
//            )
        }
    }

    private fun sendTokenBookingFailedEvent(failureReason:String) {
        viewModelScope.launch {
            val userDetails = prefUtilApi.getUserData()
            val baseValues = getBookTokenValuesMap().toMutableMap()
            baseValues["failure_reason"] = failureReason
            baseValues["fleet_name"] = userDetails?.bikeProvider.toString()
//            analyticsApi.postEvent(
//                event = "token_booking_failed",
//                values = baseValues
//            )
        }
    }

    private fun sendBookTokenEvent(tokenNumber: String,tokenId:String, isDiy: Boolean) {
        sendHomeScreenSessionDurationEvent()
        viewModelScope.launch {
            val userDetails = prefUtilApi.getUserData()
            val baseValues = getBookTokenValuesMap().toMutableMap()
            baseValues["token_number"] = tokenNumber
            baseValues["token_id"] = tokenId
            baseValues["is_diy"] = isDiy.toString()
            baseValues["fleet_name"] = userDetails?.bikeProvider.toString()
//            analyticsApi.postEvent(
//                event = "token_booked",
//                values = baseValues
//            )
        }
    }

    private fun sendHomeScreenSessionDurationEvent(){
        viewModelScope.launch {
            val currentUser = prefUtilApi.getUserData()
//            analyticsApi.postEvent(
//                event = "homescreen_session_duration",
//                values = mapOf(
//                    "user_id" to currentUser?.userId.orEmpty(),
//                    "name" to "${currentUser?.firstName.orEmpty()} ${currentUser?.surname.orEmpty()}",
//                    "mobile_number" to currentUser?.phone.orEmpty(),
//                    "session_duration" to (currentTimeMillis()- mapState.value.launchTimeStamp.orZero()),
//                    "number_of_stations_selected" to mapState.value.numberOfStationsSelected,
//                    "bike_qr_number" to currentUser?.clientVehicleQrCode.orEmpty()
//                )
//            )
        }
    }

    fun sendHomeScreenViewedEvent() {
        viewModelScope.launch {
            val currentUser = prefUtilApi.getUserData()
//            analyticsApi.postEvent(
//                event = "screen_viewed",
//                values = mapOf(
//                    "user_id" to currentUser?.userId.orEmpty(),
//                    "name" to "${currentUser?.firstName.orEmpty()} ${currentUser?.surname.orEmpty()}",
//                    "mobile_number" to currentUser?.phone.orEmpty(),
//                    "screen_name" to "Home_Screen",
//                    "timestamp" to currentTimeMillis(),
//                    "bike_qr_number" to currentUser?.clientVehicleQrCode.orEmpty(),
//                    "fleet_name" to currentUser?.bikeProvider.toString()
//                )
//            )
        }
    }

    private fun getBookTokenValuesMap(): Map<String, Any> {
        val currentUser = mapState.value.userDetails
        val currentStation = mapState.value.selectedStation

        return mapOf(
            "user_id" to currentUser?.userId.orEmpty(),
            "name" to "${currentUser?.firstName.orEmpty()} ${currentUser?.surname.orEmpty()}",
            "mobile_number" to currentUser?.phone.orEmpty(),
            "timestamp" to currentTimeMillis(),
            "distance" to (currentStation?.stationCurrentStatus?.distanceInMeters?.toString().orEmpty()),
            "station_id" to (currentStation?.stationId?.toString().orEmpty()),
            "station_name" to currentStation?.stationName.orEmpty(),
            "battery_details" to mapState.value.batteryDetails.toString(),
            "bike_qr_number" to currentUser?.clientVehicleQrCode.orEmpty(),
        )
    }

    private fun loadBatteryDetails(){
        println("Getting Battery Details")
        viewModelScope.launch {
            val clientId =  mapState.value.userDetails?.clientVehicleId.orZero()
            getBatteryDetailsUseCase.invoke(
                clientVehicleId = clientId
            ).collect(
                onLoading = {},
                onSuccess = {
                    _mapState.value = _mapState.value.copy(
                        batteryDetails = it
                    )
                },
                onError = { errorMessage, _ -> }
            )
        }
    }

    private fun sendGoogleMapApiEvent() {
        viewModelScope.launch {
//            analyticsApi.postEvent(
//                event = "google_maps_api_called",
//                values = mapOf(
//                    "user_id" to cachedUser?.userId.orEmpty(),
//                    "timestamp" to currentTimeMillis(),
//                    "mobile_number" to cachedUser?.phone.orEmpty(),
//                )
            //)
        }
    }


}

sealed class MapScreenUiEvent {
    data object TokenBooked : MapScreenUiEvent()
    data class ShowSnackbar(val message: String) : MapScreenUiEvent()
    data object NavigateToTagBattery : MapScreenUiEvent()
}
