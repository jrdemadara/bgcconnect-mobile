package org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.local

import kotlinx.coroutines.flow.Flow
import org.jrdemadara.MessageReactions

interface MessageReactionRepository {
    suspend fun insertReaction(remoteId: Long, messageId: Long, userId: Long, reaction: String)
    suspend fun reactToMessage(messageId: Long, userId: Long, reaction: String)
    suspend fun updateReactionSendStatus(id: Long, remoteId: Long?, sendStatus: String)
    suspend fun deleteReaction(id: Long, soft: Boolean)
    suspend fun getPendingReactions(): Flow<List<MessageReactions>>

}