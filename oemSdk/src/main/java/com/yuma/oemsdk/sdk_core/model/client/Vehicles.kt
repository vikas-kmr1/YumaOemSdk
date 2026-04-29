package com.yumaoem.core.model.client

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

typealias CoreClientVehicle = ClientVehicles

fun ClientVehiclesItemDTO.toCore(): CoreClientVehicle =
    ClientVehicles(
        itemGroupId = itemGroupId,
        qrCode = qrCode,
        clientId = clientId,
        clientCityId = clientCityId,
        clientVehicleId = clientVehicleId,
        bikeProvider = bikeProvider,
        bikeNumber = bikeNumber
    )


data class ClientVehicles(
    val itemGroupId: Int,
    val qrCode: String,
    val clientId: Int,
    val clientCityId: Int,
    val clientVehicleId: Int,
    val bikeProvider: String,
    val bikeNumber: String
)

@Serializable
data class ClientVehiclesItemDTO(

    @SerialName("item_group_id")
    val itemGroupId: Int,

    @SerialName("qr_code")
    val qrCode: String,

    @SerialName("client_id")
    val clientId: Int,

    @SerialName("client_city_id")
    val clientCityId: Int,

    @SerialName("client_vehicle_id")
    val clientVehicleId: Int,

    @SerialName("bike_provider")
    val bikeProvider: String,

    @SerialName("bike_number")
    val bikeNumber: String
)

