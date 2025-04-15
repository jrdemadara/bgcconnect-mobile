package org.jrdemadara.bgcconnect.util

fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.lowercase().replaceFirstChar(Char::uppercase) }