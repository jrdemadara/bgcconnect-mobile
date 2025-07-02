package org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.impl

import org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.api.MessageReactionApi
import org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.dto.MessageReactionResponse
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.remote.repository.MessageReactionRepository

class MessageReactionRepositoryImpl(private val messageReactionApi: MessageReactionApi) :
    MessageReactionRepository {
    override suspend fun send(messageId: Int, userId: Int, reaction: String, token: String): MessageReactionResponse {
        return messageReactionApi.send(messageId, userId, reaction, token)
   }
}