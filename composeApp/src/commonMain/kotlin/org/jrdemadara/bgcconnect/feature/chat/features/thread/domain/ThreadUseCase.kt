package org.jrdemadara.bgcconnect.feature.chat.features.thread.domain

class SendMessageUseCase(
    private val repository: ThreadRepository
) {
    suspend operator fun invoke(localId:Int, chatId: Int, content: String, messageType: String, replyTo: Int?, tempId: Int, token: String): String {
        try {
            val result = repository.sendMessage(localId, chatId, content, messageType, replyTo, tempId, token)
            return result
        } catch (e: Exception) {
            throw Exception("${e.message}")
        }
    }
}

class MarkAsReadUseCase(
    private val repository: ThreadRepository
) {
    suspend operator fun invoke(chatId: Int, messageIds: Int, token: String): String {
        try {
            val result = repository.markAsRead(chatId, messageIds, token)
            return result
        }catch (e: Exception) {
            throw Exception("${e.message}")
        }
    }
}
