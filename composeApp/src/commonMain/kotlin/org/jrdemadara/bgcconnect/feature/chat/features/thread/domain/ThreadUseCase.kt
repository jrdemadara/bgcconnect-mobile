package org.jrdemadara.bgcconnect.feature.chat.features.thread.domain

class SendMessageUseCase(
    private val repository: ThreadRepository
) {
    suspend operator fun invoke(chatId: Int, content: String, messageType: String, replyTo: Int, token: String): String {
        try {
            val result = repository.sendMessage(chatId, content, messageType, replyTo, token)
            return result
        } catch (e: Exception) {
            throw Exception("${e.message}")
        }
    }
}
