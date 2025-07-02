package org.jrdemadara.bgcconnect.feature.chat.features.thread.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.jrdemadara.bgcconnect.core.local.SessionManager
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.local.DeleteReactionUseCase
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.local.GetPendingReactionsUseCase
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.local.ReactToMessageUseCase
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.local.UpdateReactionStatusUseCase
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.remote.usecases.SendMessageReactionUseCase
import kotlin.time.ExperimentalTime

class MessageReactionViewModel(
    sessionManager: SessionManager,
    private val reactToMessage: ReactToMessageUseCase,
    private val deleteReaction: DeleteReactionUseCase,
    private val updateReactionStatus: UpdateReactionStatusUseCase,
    private val getPendingReactions: GetPendingReactionsUseCase,
    private val sendReaction: SendMessageReactionUseCase
) : ViewModel() {
    private val token: String = sessionManager.getToken().toString()
    private val id: Int = sessionManager.getUserId()

    init {
        observePendingMessageReactions()
    }

    fun onReact(messageId: Long, userId: Long, reaction: String) {
        viewModelScope.launch {
            reactToMessage(messageId = messageId, userId = userId, reaction = reaction)
        }
    }

    fun onRemoveReaction(id: Long, soft: Boolean) {
        viewModelScope.launch {
            deleteReaction(
                id = id,
                soft = soft
            )
        }
    }

    private fun observePendingMessageReactions() {
        println("Observing Pending Reactions")
        viewModelScope.launch {
            getPendingReactions().collect { pendingReactions ->
                println("Reaction: $pendingReactions")
                pendingReactions.forEach { reaction ->
                    println("Reaction: $reaction")
                    try {
                        val response = sendReaction(
                            messageId = reaction.messageId.toInt(),
                            userId = reaction.userId.toInt(),
                            reaction = reaction.reaction,
                            token = token
                        )

                        updateReactionStatus.invoke(
                            id = reaction.id,
                            remoteId = response.remoteId.toLong(),
                            sendStatus = "sent"
                        )

                    } catch (e: Exception) {
                        updateReactionStatus.invoke(
                            id = reaction.id,
                            remoteId = null,
                            sendStatus = "failed"
                        )
                    }
                }
            }
        }
    }
}