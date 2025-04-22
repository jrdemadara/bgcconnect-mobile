package org.jrdemadara.bgcconnect.core

import kotlinx.coroutines.flow.Flow

actual class PusherManager {
    actual fun connectToUserChannel(userId: String) {
    }

    actual fun disconnect() {
    }

    actual val incomingRequests: Flow<String>
        get() = TODO("Not yet implemented")
    actual val messageRequests: Flow<String>
        get() = TODO("Not yet implemented")
    actual val chatCreated: Flow<String>
        get() = TODO("Not yet implemented")
    actual val chatReceived: Flow<String>
        get() = TODO("Not yet implemented")
    actual val chatDelivered: Flow<String>
        get() = TODO("Not yet implemented")
    actual val chatSeen: Flow<String>
        get() = TODO("Not yet implemented")
    actual val typing: Flow<String>
        get() = TODO("Not yet implemented")
    actual val userStatus: Flow<String>
        get() = TODO("Not yet implemented")
}