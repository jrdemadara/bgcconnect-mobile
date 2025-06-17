package org.jrdemadara.bgcconnect.util

import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime


fun dateNowIso(): String {
    return try {
        // Get the current instant and convert it to LocalDateTime in the system's time zone
        val currentInstant = Clock.System.now()
        val currentDateTime = currentInstant.toLocalDateTime(TimeZone.UTC)

        // Manually format the date and time
        val formattedDate = "${currentDateTime.year.toString().padStart(4, '0')}-" +
                "${(currentDateTime.monthNumber).toString().padStart(2, '0')}-" +
                "${currentDateTime.dayOfMonth.toString().padStart(2, '0')} " +
                "${currentDateTime.hour.toString().padStart(2, '0')}:" +
                "${currentDateTime.minute.toString().padStart(2, '0')}:" +
                "${currentDateTime.second.toString().padStart(2, '0')}." +
                "${(currentDateTime.nanosecond / 1_000_000).toString().padStart(3, '0')}"

        formattedDate
    } catch (e: Exception) {
        "Error formatting date" // Fallback in case of an error
    }
}