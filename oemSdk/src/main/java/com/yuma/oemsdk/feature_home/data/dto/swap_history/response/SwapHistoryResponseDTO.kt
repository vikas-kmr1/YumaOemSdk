package com.yumaoem.feature_home.data.dto.swap_history.response

import com.yumaoem.feature_home.domain.model.profile.swap_history.SwapHistoryItem
import com.yumaoem.feature_home.domain.model.profile.swap_history.SwapsItem
import kotlinx.datetime.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class SwapHistoryResponseDTO(
    @SerialName("meta")
    val meta: Meta,

    @SerialName("swapHistory")
    val swapHistory: List<SwapHistoryGroupDTO>
)

@Serializable
data class SwapHistoryGroupDTO(
    @SerialName("service_date")
    val serviceDate: String,

    @SerialName("swaps")
    val swaps: List<SwapHistoryItemDTO>
)

@Serializable
data class SwapHistoryItemDTO(
    @SerialName("service_time")
    val serviceTime: String,

    @SerialName("service_date")
    val serviceDate: String,

    @SerialName("commander_name")
    val commanderName: String,

    @SerialName("created_timestamp")
    val createdTimestamp: Int,

    @SerialName("commander_id")
    val commanderId: String,

    @SerialName("serviced_at")
    val servicedAt: String
)

@Serializable
data class Meta(
    @SerialName("total")
    val total: Int,

    @SerialName("limit")
    val limit: Int,

    @SerialName("totalPages")
    val totalPages: Int,

    @SerialName("page")
    val page: Int,

    @SerialName("totalSwaps")
    val totalSwaps: Int
)


fun SwapHistoryResponseDTO.toDomain(): List<SwapHistoryItem> {
    return swapHistory.map { group ->
        val swaps = group.swaps
            .map { dto ->
                SwapsItem(
                    serviceTime = dto.serviceTime,
                    swapTime = dto.servicedAt,
                    icon = null
                )
            }

        SwapHistoryItem(
            date = group.serviceDate,
            swaps = swaps
        )
    }
}


//@Serializable
//data class SwapHistoryResponseDTO(
//
//    @SerialName("meta")
//    val meta: Meta,
//
//    @SerialName("swapHistory")
//    val swapHistory: List<SwapHistoryItemDTO>
//)
//
//@Serializable
//data class SwapHistoryItemDTO(
//
//    @SerialName("service_time")
//    val serviceTime: String,
//
//    @SerialName("service_date")
//    val serviceDate: String,
//
//    @SerialName("commander_name")
//    val commanderName: String,
//
//    @SerialName("created_timestamp")
//    val createdTimestamp: Int,
//
//    @SerialName("commander_id")
//    val commanderId: String
//)
//
//@Serializable
//data class Meta(
//
//    @SerialName("total")
//    val total: Int,
//
//    @SerialName("limit")
//    val limit: Int,
//
//    @SerialName("totalPages")
//    val totalPages: Int,
//
//    @SerialName("page")
//    val page: Int
//)


//
///** Convert a flat list of DTOs to the grouped, UI-ready model. */
//fun SwapHistoryResponseDTO.toDomain(
//    timeZone: TimeZone = TimeZone.currentSystemDefault()
//): List<SwapHistoryItem> {
//
//    val source = this.swapHistory
//    // 1️⃣ Group every DTO by "calendar day" in the user's zone
//    val groupedByDate: Map<LocalDate, List<SwapHistoryItemDTO>> =
//        source.groupBy { dto ->
//            dto.createdTimestamp.toInstant()
//                .toLocalDateTime(timeZone)
//                .date                     // ← LocalDate(2025-05-29)
//        }
//
//    // 2️⃣ Turn each bucket into a SwapHistoryItem and sort by date descending
//    return groupedByDate
//        .toList()                                           // Convert to list of pairs
//        .sortedByDescending { (localDate, _) -> localDate } // Sort by LocalDate descending
//        .map { (localDate, itemsInThatDay) ->
//
//            val swaps = itemsInThatDay
//                .sortedByDescending { it.createdTimestamp }  // newest → oldest inside that day
//                .map { dto ->
//                    SwapsItem(
//                        serviceTime = dto.serviceTime,
//                        swapTime    = dto.createdTimestamp.toInstant()
//                            .toLocalDateTime(timeZone)
//                            .formatAsTime(),    //  "5:42 PM"
//                        icon        =  null
//                    )
//                }
//
//            SwapHistoryItem(
//                date  = localDate.formatAsDate(),            // "29 May"
//                swaps = swaps
//            )
//        }
//}
//
//private fun Int.toInstant(): Instant = Instant.fromEpochSeconds(toLong())
//
///** "29 May" (short english month-name, no leading zero) */
//private fun LocalDate.formatAsDate(): String =
//    "${dayOfMonth} ${monthNamesFull[monthNumber - 1]}"
//
///** "5:42 PM", "10 AM", "12 PM" */
//private fun LocalDateTime.formatAsTime(): String {
//    val rawHour   = hour % 12
//    val displayH  = if (rawHour == 0) 12 else rawHour          // 0→12, 13→1, etc.
//    val minuteStr = if (minute == 0) ""                       // "10 AM" instead of "10:00 AM"
//    else ":${minute.toString().padStart(2, '0')}"
//    val amPm      = if (hour < 12) "AM" else "PM"
//    return "$displayH$minuteStr $amPm"
//}
//
///** Very simple month list (short form, English). */
//private val monthNames = listOf(
//    "Jan","Feb","Mar","Apr","May","Jun",
//    "Jul","Aug","Sep","Oct","Nov","Dec"
//)
//
///** Full month names (English). */
//private val monthNamesFull = listOf(
//    "January", "February", "March", "April", "May", "June",
//    "July", "August", "September", "October", "November", "December"
//)
//
//
