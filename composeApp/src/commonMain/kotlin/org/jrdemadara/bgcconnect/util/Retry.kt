package org.jrdemadara.bgcconnect.util

import kotlinx.coroutines.delay

suspend fun retry(times: Int, delayMillis: Long = 1000, block: suspend () -> Unit) {
    repeat(times - 1) { attempt ->
        try {
            block()
            return
        } catch (e: Exception) {
            println("❌ Attempt ${attempt + 1} failed: ${e.message}")
            delay(delayMillis)
        }
    }
    block() // final attempt
}