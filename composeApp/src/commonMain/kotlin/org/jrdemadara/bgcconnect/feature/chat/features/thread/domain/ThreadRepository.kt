package org.jrdemadara.bgcconnect.feature.chat.features.thread.domain

interface ThreadRepository {
    suspend fun sendMessage(chatId: Int, content: String, messageType: String, replyTo: Int, token: String): String
}