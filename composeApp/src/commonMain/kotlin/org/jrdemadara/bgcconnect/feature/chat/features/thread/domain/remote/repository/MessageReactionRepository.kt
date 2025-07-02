package org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.remote.repository

import org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.dto.MessageReactionResponse

interface MessageReactionRepository {
    suspend fun send(messageId: Int, userId: Int, reaction: String, token: String): MessageReactionResponse
}