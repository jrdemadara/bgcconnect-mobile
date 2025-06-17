package org.jrdemadara.bgcconnect.feature.chat.data.local.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.jrdemadara.MessageStatus
import org.jrdemadara.MessageStatusQueries

class MessageStatusDao(private val queries: MessageStatusQueries) {
    fun getStatusByMessageId(messageId: Long): Flow<List<MessageStatus>> =
        queries.selectStatusByMessageId(messageId).asFlow().mapToList(Dispatchers.Default)

    fun insertStatus(status: MessageStatus) {
        queries.insertStatus(
            id = status.id,
            messageId = status.messageId,
            userId = status.userId,
            status = status.status,
            updatedAt = status.updatedAt
        )
    }

    fun markMessagesAsRead(updatedAt: String, messageId: Int, userId: Int) {
            queries.updateStatus(
                status = "read",
                updatedAt = updatedAt,
                messageId = messageId.toLong(),
                userId = userId.toLong())
    }
}