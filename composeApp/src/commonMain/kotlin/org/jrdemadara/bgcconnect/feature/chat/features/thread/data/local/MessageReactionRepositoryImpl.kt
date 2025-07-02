package org.jrdemadara.bgcconnect.feature.chat.features.thread.data.local

import kotlinx.coroutines.flow.Flow
import org.jrdemadara.MessageReactions
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.local.MessageReactionRepository
import org.jrdemadara.bgcconnect.getCurrentTimestamp

class MessageReactionRepositoryImpl(
    private val dao: MessageReactionDao
) : MessageReactionRepository {
    override suspend fun insertReaction(remoteId: Long, messageId: Long, userId: Long, reaction: String) {
        val now = getCurrentTimestamp.nowIsoUtc()
        val existing = dao.getReactionByMessageAndUser(messageId, userId)
        if (existing == null) {
            dao.insertReaction(
                remoteId =  remoteId,
                messageId =  messageId,
                userId =  userId,
                reaction =  reaction,
                sendStatus =  "received",
                updatedAt =  now)
        } else {
            dao.updateReaction(
                messageId = messageId,
                userId = userId,
                reaction = reaction,
                sendStatus =  "received",
                updatedAt = now
            )
        }
    }

    override suspend fun reactToMessage(messageId: Long, userId: Long, reaction: String) {
        val now = getCurrentTimestamp.nowIsoUtc()
        val existing = dao.getReactionByMessageAndUser(messageId, userId)
        if (existing == null) {
            dao.insertReaction(
                remoteId = null,
                messageId = messageId,
                userId = userId,
                reaction = reaction,
                sendStatus = "pending",
                updatedAt = now
            )
        } else if (existing.reaction != reaction) {
            dao.updateReaction(
                messageId = messageId,
                userId = userId,
                reaction = reaction,
                sendStatus =  "pending",
                updatedAt = now
            )
        }
    }

    override suspend fun updateReactionSendStatus(id: Long, remoteId: Long?, sendStatus: String) {
        val now = getCurrentTimestamp.nowIsoUtc()
        dao.updateSendStatus(id = id, remoteId =  remoteId, sendStatus =  sendStatus, updatedAt =  now)
    }

    override suspend fun deleteReaction(id: Long, soft: Boolean) {
        if (soft) dao.softDelete(id = id) else dao.hardDelete(id = id)
    }

    override suspend fun getPendingReactions(): Flow<List<MessageReactions>> {
       return dao.getPendingReactions()
    }
}