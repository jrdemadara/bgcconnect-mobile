package org.jrdemadara.bgcconnect.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jrdemadara.MessageRequest
import org.jrdemadara.bgcconnect.core.local.SessionManager
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.data.IncomingRequest
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.data.MessageRequestDao

class RealtimeEventManager(
    private val pusherManager: PusherManager,
    sessionManager: SessionManager,
    private val messageRequestDao: MessageRequestDao,
   // private val chatDao: ChatDao // Optional for other events
) {
    private val id = sessionManager.getUserId()
    fun start() {
        observeMessageRequests()
        observeChatReceived()
        // Add more observers here if needed
    }

    private fun observeMessageRequests() {
        CoroutineScope(Dispatchers.Default).launch {
            pusherManager.messageRequests.collect { rawJson ->
                try {
                    val dto = Json.decodeFromString<IncomingRequest>(rawJson)

                    // Map DTO to SQLDelight model
                    val request = MessageRequest(
                        id = 0, // Auto-increment
                        senderId = dto.sender.id.toLong(),
                        firstname = dto.sender.firstname,
                        lastname = dto.sender.lastname,
                        avatar = dto.sender.avatar,
                        recipientId = id.toLong(),
                        status = "pending",
                        requestedAt = dto.requestedAt
                    )
                    messageRequestDao.insert(request)
                    println("📥 MessageRequest saved: ${request.firstname}")
                } catch (e: Exception) {
                    println("❌ Failed to parse MessageRequest: ${e.message}")
                }
            }
        }
    }

    private fun observeChatReceived() {
        CoroutineScope(Dispatchers.Default).launch {
            pusherManager.chatReceived.collect { json ->
                // TODO: parse and save to chatDao
            }
        }
    }
}