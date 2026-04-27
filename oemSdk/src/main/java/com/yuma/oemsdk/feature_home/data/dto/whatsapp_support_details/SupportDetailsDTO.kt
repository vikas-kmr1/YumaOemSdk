package com.yumaoem.feature_home.data.dto.whatsapp_support_details

import com.yumaoem.feature_home.domain.model.supportDetails.SupportDetails
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class SupportDetailsDTO(

	@SerialName("whatsapp_message")
	val whatsappMessage: String,

	@SerialName("whatsapp_number")
	val whatsappNumber: Long
)

fun SupportDetailsDTO.toDomain() = SupportDetails(
	defaultMessage = whatsappMessage,
	phoneNumber = whatsappNumber
)
