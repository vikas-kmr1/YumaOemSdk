package com.yumaoem.feature_home.data.dto.station_operation_status.response

import com.yumaoem.feature_home.data.dto.maps_screen.all_stations.Coordinates
import com.yumaoem.feature_home.data.dto.maps_screen.all_stations.OperationStatus
import com.yumaoem.feature_home.data.dto.maps_screen.all_stations.toDomain
import com.yumaoem.feature_home.domain.model.maps.all_station_markers.YumaStationMarker
import com.yumaoem.feature_home.domain.model.maps.all_station_markers.YumaStationStatus
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.ChargingStationState
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel.LatLong
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StationOperationStatusResponseDTO(
	@SerialName("csName")
	val csName: String,

	@SerialName("csId")
	val csId: Int,

	@SerialName("coordinates")
	val coordinates: Coordinates,

	@SerialName("operationStatus")
	val operationStatus: OperationStatus
)


fun StationOperationStatusResponseDTO.toYumaStationMarker() : YumaStationMarker {
	return YumaStationMarker(
		stationName = csName,
		stationId = csId,
		location = LatLong(coordinates.latitude, coordinates.longitude),
		stationCurrentStatus = operationStatus.toDomain()
	)
}
fun StationOperationStatusResponseDTO.toDomain(): YumaStationStatus {
	val stationState = operationStatus.currentStatus.stationStatusId.toChargingStationState()

	return YumaStationStatus(
			currentTime = operationStatus.currentStatus.currentTime,
			nextOpeningTime = operationStatus.currentStatus.nextOpeningTime,
			nextClosingTime = operationStatus.currentStatus.nextClosingTime,
			stationStatusId = operationStatus.currentStatus.stationStatusId,
			stationState = stationState,
		    distanceFromUser = ""
		)
}


fun Int.toChargingStationState(): ChargingStationState {
	return when (this) {
		1 -> ChargingStationState.OPEN
		2 -> ChargingStationState.CLOSED
		3 -> ChargingStationState.BREAK_TIME
		4 -> ChargingStationState.BATTERY_NOT_AVAILABLE
		5 -> ChargingStationState.TEMPORARILY_UNAVAILABLE
		6 -> ChargingStationState.DIY_ALWAYS_OPEN
		7 -> ChargingStationState.DIY_WITH_TIMING
		else -> ChargingStationState.NOT_AVAILABLE
	}
}

/**
 * Returns true if the station is considered "open" for the user.
 */
fun ChargingStationState.isOperational(): Boolean {
	return when (this) {
		ChargingStationState.OPEN,
		ChargingStationState.DIY_ALWAYS_OPEN,
		ChargingStationState.DIY_WITH_TIMING -> true
		else -> false
	}
}







