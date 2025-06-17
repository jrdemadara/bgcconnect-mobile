package org.jrdemadara.bgcconnect.core.firebase

import com.russhwolf.settings.Settings
import org.jrdemadara.MessageRequest
import org.jrdemadara.bgcconnect.data.FirebaseMessageRequest
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.data.MessageRequestDao

class FirebaseEventManager: FirebaseManager {
    private var messageRequestDao: MessageRequestDao? = null
    val settings: Settings = Settings()
    val userId = settings.getInt(
        "id",
        defaultValue = 0
    )
    fun init(dao: MessageRequestDao) {
        this.messageRequestDao = dao
    }

    override fun onMessageReceived(message: FirebaseMessageRequest) {
        println("Shared message received: ${message}")
        val request = MessageRequest(
            id = message.data.id.toLong(),
            senderId = message.data.sender.toLong(),
            firstname = message.data.firstname,
            lastname = message.data.lastname,
            avatar = message.data.avatar,
            recipientId = userId.toLong(),
            status = message.data.status,
            requestedAt = message.data.requested_at
        )
        messageRequestDao?.insert(request)
        println("📥 MessageRequest saved: ${request.firstname}")
    }

    override fun subscribeToTopic(topic: String) {
//        println("✅ Subscribed to topic: $topic")
    }

    override fun unsubscribeFromTopic(topic: String) {
//        println("✅ Unsubscribed to topic: $topic")
    }
}