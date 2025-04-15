package org.jrdemadara.bgcconnect.feature.chat.features.message_request.data

import io.ktor.client.statement.HttpResponse
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.domain.MessageRequestRepository

class MessageRequestRepositoryImpl(private val api: MessageRequestApi) : MessageRequestRepository {
    override suspend fun messageRequest(recipientId: Int, token: String): String {
        return api.messageRequest(recipientId, token)
    }
}