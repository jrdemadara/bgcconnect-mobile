package org.jrdemadara.bgcconnect.feature.chat.features.message_request.data

import io.ktor.client.statement.HttpResponse
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.domain.MessageRequestRepository

class MessageRequestRepositoryImpl(private val api: MessageRequestApi) :
    MessageRequestRepository {
    override suspend fun sendMessageRequest(recipientId: Int, token: String): String {
        return api.sendMessageRequest(recipientId, token)
    }

    override suspend fun acceptRequest(id: Int, token: String): String {
        return api.acceptMessageRequest(id, token)
    }

    override suspend fun deleteRequest(id: Int, token: String): String {
        return api.declineMessageRequest(id, token)
    }
}