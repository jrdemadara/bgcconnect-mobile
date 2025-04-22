package org.jrdemadara.bgcconnect.feature.chat.data.local.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.jrdemadara.ChatParticipant
import org.jrdemadara.ChatParticipantsQueries

class ChatParticipantDao(private val queries: ChatParticipantsQueries) {
    fun getParticipantsByChatId(chatId: Long): Flow<List<ChatParticipant>> =
        queries.selectParticipantsByChatId(chatId).asFlow().mapToList(Dispatchers.Default)

    suspend fun insertParticipant(participant: ChatParticipant) {
        queries.insertParticipant(
            chatId = participant.chatId,
            userId = participant.userId,
            role = participant.role,
            joinedAt = participant.joinedAt,
            createdAt = participant.createdAt,
            updatedAt = participant.updatedAt
        )
    }
}