package org.jrdemadara.bgcconnect.core.firebase

import kotlinx.coroutines.flow.Flow

actual class FirebaseManager actual constructor() {
    actual fun init() {
    }

    actual val tokenFlow: Flow<String>
        get() = TODO("Not yet implemented")
    actual val messageFlow: Flow<FcmMessage>
        get() = TODO("Not yet implemented")
}