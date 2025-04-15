package org.jrdemadara.bgcconnect.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.data.IncomingRequest

class RealtimeEventManager(
    private val pusherManager: PusherManager,
    //private val messageRequestDao: MessageRequestDao, // SQLDelight DAO
   // private val chatDao: ChatDao // Optional for other events
) {

    fun start() {
        observeMessageRequests()
        observeChatReceived()
        // Add more observers here if needed
    }

    private fun observeMessageRequests() {
        CoroutineScope(Dispatchers.Default).launch {
            pusherManager.messageRequests.collect { json ->
                try {
                    val request = Json.decodeFromString<IncomingRequest>(json)
                   // messageRequestDao.insert(request.toEntity())
                    println("📥 MessageRequest saved: ${request.sender.firstname}")
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