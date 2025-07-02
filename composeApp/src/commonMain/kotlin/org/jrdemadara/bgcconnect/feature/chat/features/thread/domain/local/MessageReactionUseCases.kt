package org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.local

import kotlinx.coroutines.flow.Flow
import org.jrdemadara.MessageReactions

class InsertReactionUseCase(
    private val repository: MessageReactionRepository
) {
    suspend operator fun invoke(
        remoteId: Long,
        messageId: Long,
        userId: Long,
        reaction: String,
    ) {
        repository.insertReaction(remoteId, messageId, userId, reaction)
    }
}

class ReactToMessageUseCase(
    private val repository: MessageReactionRepository
) {
    suspend operator fun invoke(messageId: Long, userId: Long, reaction: String) {
        repository.reactToMessage(messageId, userId, reaction)
    }
}

class UpdateReactionStatusUseCase(
    private val repository: MessageReactionRepository
) {
    suspend operator fun invoke(id: Long, remoteId: Long?, sendStatus: String) {
        repository.updateReactionSendStatus(id, remoteId, sendStatus)
    }
}

class DeleteReactionUseCase(
    private val repository: MessageReactionRepository
) {
    suspend operator fun invoke(id: Long, soft: Boolean) {
        repository.deleteReaction(id, soft)
    }
}

class GetPendingReactionsUseCase(private val repository: MessageReactionRepository){
    suspend operator fun invoke(): Flow<List<MessageReactions>> {
        return repository.getPendingReactions()
    }
}