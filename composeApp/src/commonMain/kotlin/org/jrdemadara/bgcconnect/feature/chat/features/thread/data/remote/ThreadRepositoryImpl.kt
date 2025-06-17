package org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote

import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.ThreadRepository

class ThreadRepositoryImpl(private val threadApi: ThreadApi) : ThreadRepository {
    override suspend fun sendMessage(localId: Int, chatId: Int, content: String, messageType: String, replyTo: Int, tempId: Int, token: String): String {
        return threadApi.sendMessage(localId, chatId, content, messageType, replyTo, token)
    }

    override suspend fun markAsRead(chatId: Int, messageId: Int, token: String): String {
        return threadApi.markMessagesAsRead(chatId, messageId, token)
    }
}