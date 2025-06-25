package org.jrdemadara.bgcconnect.core.firebase

import kotlinx.coroutines.flow.Flow

expect class FirebaseManager(){
    fun init()
    val tokenFlow: Flow<String>
    val messageFlow: Flow<FirebaseMessageData>

    fun subscribeToTopic(topic: String)
    fun unsubscribeFromTopic(topic: String)
}

data class FirebaseMessageData(
    val title: String?,
    val body: String?,
    val data: Map<String, String>
)