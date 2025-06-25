package org.jrdemadara.bgcconnect.core.firebase

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.messaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

actual class FirebaseManager actual constructor() {

    private val _tokenFlow = MutableSharedFlow<String>(replay = 1)
    private val _messageFlow = MutableSharedFlow<FirebaseMessageData>(replay = 0, extraBufferCapacity = 10)

    actual val tokenFlow: Flow<String> get() = _tokenFlow
    actual val messageFlow: Flow<FirebaseMessageData> get() = _messageFlow.asSharedFlow()


    actual fun init() {
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                _tokenFlow.tryEmit(task.result)
            }
        }
    }

    fun onNewToken(token: String) {
        _tokenFlow.tryEmit(token)
    }

    fun onMessageReceived(message: RemoteMessage) {
        Log.d("FirebaseMessagingService", "✅ New message: $message")
        val fcmMessage = FirebaseMessageData(
            title = message.notification?.title,
            body = message.notification?.body,
            data = message.data
        )


        val emitted = _messageFlow.tryEmit(fcmMessage)
        Log.d("FirebaseManager", "📤 [${message}] emission status: $emitted")
    }

    actual fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "✅ Subscribed to topic: $topic")
                } else {
                    Log.e("FCM", "❌ Failed to subscribe to topic: $topic", task.exception)
                }
            }
    }

    actual fun unsubscribeFromTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "✅ Unsubscribed from topic: $topic")
                } else {
                    Log.e("FCM", "❌ Failed to unsubscribe from topic: $topic", task.exception)
                }
            }
    }
}