package com.yumaoem.feature_home.data.dto.maps_screen.all_stations

import com.yumaoem.core.utils.orFalse
import com.yumaoem.feature_home.data.dto.station_operation_status.response.toChargingStationState
import com.yumaoem.feature_home.domain.model.maps.all_station_markers.YumaStationMarker
import com.yumaoem.feature_home.domain.model.maps.all_station_markers.YumaStationStatus
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.ChargingStationState
import com.yumaoem.feature_home.presentation.home_screen.maps_screen.viewmodel.LatLong
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NearbyStationsResponseDTO(
	@SerialName("stations")
	val stations: List<StationsItem>
)

@Serializable
data class StationsItem(
	@SerialName("csName")
	val csName: String,

	@SerialName("csId")
	val csId: Int,

	@SerialName("coordinates")
	val coordinates: Coordinates,

	@SerialName("operationStatus")
	val operationStatus: OperationStatus? = null,
)

@Serializable
data class Coordinates(

	@SerialName("latitude")
	val latitude: Double,

	@SerialName("longitude")
	val longitude: Double
)


@Serializable
data class OperationStatus(

	@SerialName("dayOfWeek")
	val dayOfWeek: Int? = null,

	@SerialName("currentStatus")
	val currentStatus: CurrentStatus,

	@SerialName("stationId")
	val stationId: Int? = null,

	@SerialName("stationTitle")
	val title:String? = null,

	@SerialName("isAmongNearestStations")
	val isAmongNearestStations: Boolean? = null
)

@Serializable
data class CurrentStatus(

	@SerialName("currentTime")
	val currentTime: Long,

	@SerialName("nextOpeningTime")
	val nextOpeningTime: Int? = null,

	@SerialName("nextClosingTime")
	val nextClosingTime: Int? = null,

	@SerialName("stationStatusId")
	val stationStatusId: Int
)

fun NearbyStationsResponseDTO.toDomain(): List<YumaStationMarker> {
	return this.stations.map { it.toDomain() }
}


fun StationsItem.toDomain(): YumaStationMarker {
	return YumaStationMarker(
		stationId = this.csId,
		stationName = this.csName,
		title = this.operationStatus?.title,
		location = LatLong(
			latitude = this.coordinates.latitude,
			longitude = this.coordinates.longitude
		),
		stationCurrentStatus = this.operationStatus?.toDomain() ?: YumaStationStatus()
	)
}

fun OperationStatus.toDomain(): YumaStationStatus {
	val stationState = currentStatus.stationStatusId.toChargingStationState()
	return YumaStationStatus(
		currentTime = currentStatus.currentTime,
		nextOpeningTime = currentStatus.nextOpeningTime,
		nextClosingTime = currentStatus.nextClosingTime,
		stationStatusId = currentStatus.stationStatusId,
		stationState = stationState,
		isAmongNearestStations = this.isAmongNearestStations.orFalse(),
		distanceFromUser = ""
	)
}

fun OperationStatus.toDomain2(): YumaStationStatus {
	val stationState = currentStatus.stationStatusId.toChargingStationState()
	return YumaStationStatus(
		currentTime = currentStatus.currentTime,
		nextOpeningTime = currentStatus.nextOpeningTime,
		nextClosingTime = currentStatus.nextClosingTime,
		stationStatusId = currentStatus.stationStatusId,
		stationState = stationState,
		isAmongNearestStations = true,
		distanceFromUser = ""
	)
}




