package org.jrdemadara.bgcconnect.feature.chat.features.message_request.domain

interface MessageRequestRepository {
    suspend fun sendMessageRequest(recipientId: Int, token: String): String
    suspend fun acceptRequest(id: Int, token: String): String
    suspend fun deleteRequest(id: Int, token: String): String
}