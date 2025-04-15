package org.jrdemadara.bgcconnect.feature.chat.features.message_request.domain

class MessageRequestUseCase(
    private val repository: MessageRequestRepository
) {
    suspend operator fun invoke(recipientId: Int, token: String): String {
        try {
            val request = repository.messageRequest(recipientId, token)
            return request
        } catch (e: Exception) {
            throw Exception("${e.message}")
        }
    }
}