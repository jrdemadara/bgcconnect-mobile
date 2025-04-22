package org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote

import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.ThreadRepository

class ThreadRepositoryImpl(private val threadApi: ThreadApi) : ThreadRepository {
    override suspend fun sendMessage(chatId: Int, content: String, messageType: String, replyTo: Int, token: String): String {
        return threadApi.sendMessage(chatId, content, messageType, replyTo, token)
    }
}