package org.jrdemadara.bgcconnect.feature.chat.data.local.dao

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.jrdemadara.Chat
import org.jrdemadara.ChatParticipantsQueries
import org.jrdemadara.ChatQueries

class ChatDao(private val queries: ChatQueries, private val chatParticipantsQueries: ChatParticipantsQueries) {
    fun getAllChats(userId: Long): Flow<List<Chat>> =
        queries.selectAllChats(userId)
            .asFlow()
            .mapToList(Dispatchers.Default)

    fun getAllChatIds(): List<Long> {
        return queries.selectAllChatIds().executeAsList()
    }

    suspend fun insertChat(chat: Chat) {
        queries.insertChat(
            id = chat.id,
            userId = chat.userId,
            chatType = chat.chatType,
            name = chat.name,
            createdAt = chat.createdAt,
            updatedAt = chat.updatedAt
        )
    }

    suspend fun deleteChat(chatId: Long) {
        queries.deleteChatById(chatId)
    }

    fun getOtherParticipantId(chatId: Long, currentUserId: Long): Long {
        return chatParticipantsQueries.getOtherParticipantId(chatId, currentUserId).executeAsOne()
    }
}