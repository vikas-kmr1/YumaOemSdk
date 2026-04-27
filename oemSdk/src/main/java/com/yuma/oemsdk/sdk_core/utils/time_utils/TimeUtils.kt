package com.yumaoem.core.utils.time_utils


/**
 * Converts the number of seconds since midnight (00:00) into a human-readable 12-hour clock time,
 * even if the input exceeds 86400 (i.e., spans into the next day).
 *
 * Example:
 * - 0         -> "12 AM"
 * - 3600      -> "1 AM"
 * - 45000     -> "12:30 PM"
 * - 86399     -> "11:59 PM"
 * - 91800     -> "1:30 AM" (next day)
 *
 * @receiver Number of seconds since 12:00 AM (may exceed 86400).
 * @return Formatted time string in "h[:mm] AM/PM" format.
 */
fun Int.formatTimeFromSeconds(): String {
    val seconds = this % 86400  // Wrap around to stay within a 24-hour range

    val totalMinutes = seconds / 60
    val hours = totalMinutes / 60
    val minutes = totalMinutes % 60

    val isPM = hours >= 12
    val displayHour = when (val h = hours % 12) {
        0 -> 12
        else -> h
    }

    val minuteStr = if (minutes < 10) "0$minutes" else "$minutes"

    return if (minutes == 0) {
        "$displayHour ${if (isPM) "PM" else "AM"}"
    } else {
        "$displayHour:$minuteStr ${if (isPM) "PM" else "AM"}"
    }
}
