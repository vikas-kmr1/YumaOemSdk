package com.yumaoem.core.utils

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.NumberFormat
import java.time.LocalDateTime
import java.util.Locale
import kotlin.math.abs
import kotlin.math.log10

fun Int?.orZero() = this ?: 0

fun Long?.orZero() = this ?: 0L

fun Float?.orZero() = this ?: 0f

fun Double?.orZero() = this ?: 0.0

fun Int.length() = when (this) {
    0 -> 1
    else -> log10(abs(toDouble())).toInt() + 1
}

fun Float.addPercentage(percentage: Float): Float {
    return (this * (1 + (percentage / 100)))
}

fun Int.toBoolean(): Boolean {
    return this != 0
}

fun Long.toBoolean(): Boolean {
    return this != 0L
}

fun Float.toIntOrNull(): Int? {
    return try {
        this.toInt()
    } catch (e: Exception) {
        null
    }
}

fun Int.getFormattedAmount(maximumFractionDigits: Int): String {
    val formatter = NumberFormat.getNumberInstance(Locale("en", "in"))
    formatter.maximumFractionDigits = maximumFractionDigits
    formatter.minimumFractionDigits = 0
    return formatter.format(this)
}

fun Int.getAmountWithRupee(): String = "₹${this}"

fun Int.getFormattedAmountWithRupee(maximumFractionDigits: Int = 2): String =
    "₹" + this.getFormattedAmount(maximumFractionDigits)

fun Boolean?.orFalse(): Boolean {
    return this ?: false
}


@RequiresApi(Build.VERSION_CODES.O)
fun Long.formatToIST(): String {

    val localDateTime = LocalDateTime.now()

    val hour = localDateTime.hour % 12
    val displayHour = if (hour == 0) 12 else hour
    val minute = localDateTime.minute.toString().padStart(2, '0')
    val amPm = if (localDateTime.hour < 12) "AM" else "PM"

    return "$displayHour:$minute $amPm"
}