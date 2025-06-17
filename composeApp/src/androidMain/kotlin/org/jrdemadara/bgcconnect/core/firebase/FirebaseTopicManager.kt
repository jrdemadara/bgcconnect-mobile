package org.jrdemadara.bgcconnect.core.firebase

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import org.jrdemadara.bgcconnect.data.FirebaseMessageRequest

class FirebaseTopicManager: FirebaseManager {
    override fun onMessageReceived(message: FirebaseMessageRequest) {
        TODO("Not yet implemented")
    }

    override fun subscribeToTopic(topic: String) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)

            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("✅ Subscribed to topic: $topic")
                } else {
                    println("❌ Failed to subscribe to topic: $topic")
                    Log.e("FCM", "Failed to subscribe to topic: $topic", task.exception)
                }
            }
    }

    override fun unsubscribeFromTopic(topic: String) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "Unsubscribed from topic: $topic")
                } else {
                    Log.e("FCM", "Failed to unsubscribe from topic: $topic", task.exception)
                }
            }
    }
}