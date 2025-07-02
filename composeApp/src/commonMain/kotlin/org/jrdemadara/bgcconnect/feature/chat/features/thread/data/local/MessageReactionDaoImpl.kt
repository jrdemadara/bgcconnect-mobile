package org.jrdemadara.bgcconnect.feature.chat.features.thread.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.jrdemadara.MessageReactions
import org.jrdemadara.MessageReactionsQueries

class MessageReactionDaoImpl(
    private val queries: MessageReactionsQueries
) : MessageReactionDao {
    override suspend fun getReactionByMessageAndUser(messageId: Long, userId: Long): MessageReactions? {
        return queries.getReactionByMessageAndUser(messageId, userId).executeAsOneOrNull()
    }

    override suspend fun insertReaction(
        remoteId: Long?, messageId: Long, userId: Long, reaction: String, sendStatus: String, updatedAt: String
    ) {
        queries.insertMessageReaction(
            remoteId = remoteId,
            messageId = messageId,
            userId = userId,
            reaction = reaction,
            sendStatus = sendStatus,
            updatedAt = updatedAt
        )
    }

    override suspend fun updateReaction(
        messageId: Long, userId: Long, reaction: String, sendStatus: String, updatedAt: String
    ) {
        queries.updateMessageReaction(
            reaction = reaction,
            userId = userId,
            messageId = messageId,
            sendStatus = sendStatus,
            updatedAt = updatedAt,
        )
    }

    override suspend fun updateSendStatus(id: Long, remoteId: Long?, sendStatus: String, updatedAt: String) {
        queries.updateMessageReactionSendStatus(
            remoteId = remoteId,
            sendStatus = sendStatus,
            updatedAt = updatedAt,
            id = id
        )
    }

    override suspend fun softDelete(id: Long) {
        queries.softDeleteMessageReaction(id = id)
    }

    override suspend fun hardDelete(id: Long) {
        queries.hardDeleteMessageReaction(id= id)
    }

    override suspend fun getPendingReactions(): Flow<List<MessageReactions>> {
        return queries.getPendingReactions()
            .asFlow()
            .mapToList(Dispatchers.Default)
    }
}