package com.yumaoem.feature_home.data.dto.cu_response

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

fun deserializeCUResponse(jsonString: String, json: Json): CUResponseDTO? {
    return try {
        val jsonElement: JsonElement = json.parseToJsonElement(jsonString)
        val type = jsonElement.jsonObject["type"]?.jsonPrimitive?.content

        when (type) {
            "AccessType" -> json.decodeFromString<CUResponseDTO.AccessTypeDTO>(jsonString)
            "SystemSync" -> json.decodeFromString<CUResponseDTO.SystemSyncDTO>(jsonString)
            else -> {
                if (type != null) {
                    CUResponseDTO.UnknownTypeDTO(type)
                } else {
                    null
                }
            }
        }
    } catch (e: Exception) {
        // invalid JSON
        null
    }
}
