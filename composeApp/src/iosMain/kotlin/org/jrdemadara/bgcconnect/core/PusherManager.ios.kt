package org.jrdemadara.bgcconnect.core

import kotlinx.coroutines.flow.Flow

actual class PusherManager {
    actual fun connectToUserChannel(userId: String) {
    }

    actual fun disconnect() {
    }

    actual val incomingRequests: Flow<String>
        get() = TODO("Not yet implemented")
}