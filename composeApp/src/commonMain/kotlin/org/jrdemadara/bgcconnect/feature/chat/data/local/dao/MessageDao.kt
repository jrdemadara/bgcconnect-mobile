package org.jrdemadara.bgcconnect.feature.chat.data.local.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import org.jrdemadara.Chat
import org.jrdemadara.GetLastMessageForChat
import org.jrdemadara.Message
import org.jrdemadara.MessageQueries
import org.jrdemadara.SelectMessagesByChat


class MessageDao(private val queries: MessageQueries) {
    fun getMessagesByChat(chatId: Long): Flow<List<SelectMessagesByChat>> =
        queries.selectMessagesByChat(chatId = chatId)
            .asFlow()
            .mapToList(Dispatchers.Default)

    fun getUnsentMessages(userId: Long): Flow<List<Message>> =
        queries.getUnsentMessages(userId)
            .asFlow()
            .mapToList(Dispatchers.Default)

    fun getLastMessageForChat(userId: Long, chatId: Long): Flow<GetLastMessageForChat?> =
        queries.getLastMessageForChat(userId = userId, chatId = chatId)
            .asFlow()
            .mapToOneOrNull(context = Dispatchers.IO)

    fun isIdExists(id: Long): Boolean {
        return queries.isIdExists(id).executeAsOne()
    }

    fun insertMessage(message: Message) {
        queries.insertMessage(
            remoteId = message.remoteId,
            senderId = message.senderId,
            chatId = message.chatId,
            content = message.content,
            messageType = message.messageType,
            replyTo = message.replyTo,
            sendStatus = message.sendStatus,
            createdAt = message.createdAt,
            updatedAt = message.updatedAt
        )
    }

    fun updateMessage(message: Message, id: Long) {
        queries.updateMessage(
            id = id,
            senderId = message.senderId,
            chatId = message.chatId,
            content = message.content,
            messageType = message.messageType,
            replyTo = message.replyTo,
            sendStatus = message.sendStatus,
            createdAt = message.createdAt,
            updatedAt = message.updatedAt
        )
    }

    fun updateMessageSendStatusSent(messageId: Long, remoteId: Long, status: String, updatedAt: String, createdAt: String) {
        queries.updateMessageSent(
            remoteId = remoteId,
            status = status,
            createdAt =  createdAt,
            updatedAt = updatedAt,
            id = messageId
        )
    }

    fun updateMessageSendStatusFailed(messageId: Long, status: String, updatedAt: String) {
        queries.updateMessageFailed(
            status = status,
            updatedAt = updatedAt,
            id = messageId
        )
    }
}
