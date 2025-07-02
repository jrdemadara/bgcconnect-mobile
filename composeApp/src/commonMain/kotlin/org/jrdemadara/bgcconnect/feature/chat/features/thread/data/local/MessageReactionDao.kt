package org.jrdemadara.bgcconnect.feature.chat.features.thread.data.local

import kotlinx.coroutines.flow.Flow
import org.jrdemadara.MessageReactions

interface MessageReactionDao {
    suspend fun getReactionByMessageAndUser(messageId: Long, userId: Long): MessageReactions?

    suspend fun insertReaction(
        remoteId: Long?,
        messageId: Long,
        userId: Long,
        reaction: String,
        sendStatus: String,
        updatedAt: String
    )

    suspend fun updateReaction(
        messageId: Long,
        userId: Long,
        reaction: String,
        sendStatus: String,
        updatedAt: String
    )

    suspend fun updateSendStatus(
        id: Long,
        remoteId: Long?,
        sendStatus: String,
        updatedAt: String
    )

    suspend fun softDelete(id: Long)
    suspend fun hardDelete(id: Long)

    suspend fun getPendingReactions(): Flow<List<MessageReactions>>
}