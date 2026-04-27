package com.yumaoem.feature_home.data.dto.cu_response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface CUResponseDTO {
    @Serializable
    data class AccessTypeDTO(
        @SerialName("type") val type: String = "AccessType",
        @SerialName("response") val response: String
    ) : CUResponseDTO

    @Serializable
    data class SystemSyncDTO(
        @SerialName("type") val type: String = "SystemSync",
        @SerialName("numPorts") val numPorts: Int,
        @SerialName("batteriesInfoSystemSync") val batteriesInfo: List<SystemSyncItem>
    ) : CUResponseDTO {
        @Serializable
        data class SystemSyncItem(
            @SerialName("doorId") val doorId: Int,
            @SerialName("bin") val bin: String,
            @SerialName("soc") val soc: String,
            @SerialName("stateEnum") val stateEnum: String,
            @SerialName("chargeRemainingTime") val chargeRemainingTime: String,
            @SerialName("latchStateLocked") val latchStateLocked: Boolean
        )
    }

    @Serializable
    data class UnknownTypeDTO(
        @SerialName("type") val type: String
    ) : CUResponseDTO
}
