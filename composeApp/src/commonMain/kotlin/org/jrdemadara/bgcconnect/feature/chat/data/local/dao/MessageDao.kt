package org.jrdemadara.bgcconnect.feature.chat.data.local.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.jrdemadara.Message
import org.jrdemadara.MessageQueries

class MessageDao(private val queries: MessageQueries) {
    fun getMessagesByChat(chatId: Long): Flow<List<Message>> =
        queries.selectMessagesByChat(chatId)
            .asFlow()
            .mapToList(Dispatchers.Default)

    fun getLastMessageForChat(chatId: Long): Message? {
        return queries.getLastMessageForChat(chatId).executeAsOneOrNull()
    }

    fun insertMessage(message: Message) {
        queries.insertMessage(
            id = message.id,
            senderId = message.senderId,
            chatId = message.chatId,
            content = message.content,
            messageType = message.messageType,
            replyTo = message.replyTo,
            createdAt = message.createdAt,
            updatedAt = message.updatedAt
        )
    }
}
