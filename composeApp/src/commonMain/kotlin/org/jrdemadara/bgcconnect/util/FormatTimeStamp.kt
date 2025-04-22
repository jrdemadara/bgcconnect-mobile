package org.jrdemadara.bgcconnect.util

import kotlinx.datetime.*

fun formatTimestamp(isoDateTime: String): String {
    return try {
        val instant = Instant.parse(isoDateTime)
        val now = Clock.System.now()
        val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault())

        when {
            dateTime.date == today.date -> {
                // Today → Show time (e.g., 10:45 AM)
                val hour = if (dateTime.hour % 12 == 0) 12 else dateTime.hour % 12
                val ampm = if (dateTime.hour < 12) "AM" else "PM"
                "${hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')} $ampm"
            }

            dateTime.date == today.date.minus(DatePeriod(days = 1)) -> {
                "Yesterday"
            }

            else -> {
                // Show like Apr 20
                val monthAbbr = dateTime.month.name.lowercase()
                    .replaceFirstChar { it.uppercase() }
                    .take(3)
                "$monthAbbr ${dateTime.dayOfMonth}"
            }
        }
    } catch (e: Exception) {
        ""
    }
}