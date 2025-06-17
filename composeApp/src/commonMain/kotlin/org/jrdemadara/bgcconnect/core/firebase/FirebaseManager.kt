package org.jrdemadara.bgcconnect.core.firebase

import org.jrdemadara.bgcconnect.data.FirebaseMessageRequest

interface FirebaseManager {
    fun onMessageReceived(message: FirebaseMessageRequest)

    fun subscribeToTopic(topic: String)
    fun unsubscribeFromTopic(topic: String)
}