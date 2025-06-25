package org.jrdemadara.bgcconnect.feature.chat.features.thread.domain

interface ThreadRepository {
    suspend fun sendMessage(localId : Int, chatId: Int, content: String, messageType: String, replyTo: Int?, tempId: Int, token: String): String

    suspend fun markAsRead(chatId: Int, messageId: Int, token: String): String
}