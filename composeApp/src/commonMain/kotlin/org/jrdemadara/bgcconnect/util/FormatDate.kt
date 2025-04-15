package org.jrdemadara.bgcconnect.util

import kotlinx.datetime.*
import kotlin.time.Duration.Companion.minutes

fun formatDate(isoString: String): String {
    return try {
        val requestInstant = Instant.parse(isoString)
        val now = Clock.System.now()
        val duration = now - requestInstant

        val dateTime = requestInstant.toLocalDateTime(TimeZone.currentSystemDefault())
        val today = now.toLocalDateTime(TimeZone.currentSystemDefault()).date

        when {
            duration.inWholeMinutes < 1 -> "Just now"
            duration.inWholeMinutes < 60 -> "${duration.inWholeMinutes} mins ago"
            dateTime.date == today.minus(1, DateTimeUnit.DAY) -> "Yesterday"
            dateTime.date == today -> "${dateTime.hour}:${dateTime.minute.toString().padStart(2, '0')}"
            else -> "${dateTime.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${dateTime.dayOfMonth}, ${dateTime.year}"
        }
    } catch (e: Exception) {
        isoString // fallback
    }
}