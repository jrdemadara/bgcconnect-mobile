package org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.remote.usecases

import org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.dto.MessageReactionResponse
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.remote.repository.MessageReactionRepository

class SendMessageReactionUseCase(private val repository: MessageReactionRepository)  {
    suspend operator fun invoke(messageId: Int, userId: Int, reaction: String, token: String): MessageReactionResponse {
        try {
            val result = repository.send(messageId, userId, reaction, token)
            return result
        } catch (e: Exception) {
            throw Exception("${e.message}")
        }
    }
}