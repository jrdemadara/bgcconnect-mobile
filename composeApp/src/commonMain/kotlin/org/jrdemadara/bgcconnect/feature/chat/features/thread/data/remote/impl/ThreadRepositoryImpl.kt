package org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.impl

import org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.api.ThreadApi
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.remote.repository.ThreadRepository

class ThreadRepositoryImpl(private val threadApi: ThreadApi) : ThreadRepository {
    override suspend fun sendMessage(localId: Int, chatId: Int, content: String, messageType: String, replyTo: Int?, tempId: Int, token: String): String {
        return threadApi.sendMessage(localId, chatId, content, messageType, replyTo, token)
    }

    override suspend fun markAsRead(chatId: Int, messageId: Int, token: String): String {
        return threadApi.markMessagesAsRead(chatId, messageId, token)
    }
}