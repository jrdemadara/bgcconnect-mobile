package org.jrdemadara.bgcconnect.feature.chat.features.message_request.domain

class SendMessageRequestUseCase(
    private val repository: MessageRequestRepository
) {
    suspend operator fun invoke(recipientId: Int, token: String): String {
        try {
            val request = repository.sendMessageRequest(recipientId, token)
            return request
        } catch (e: Exception) {
            throw Exception("${e.message}")
        }
    }
}

class AcceptMessageRequestUseCase(private val repo: MessageRequestRepository) {
    suspend operator fun invoke(id: Int, token: String) {
        repo.acceptRequest(id, token)
    }
}

class DeclineMessageRequestUseCase(private val repo: MessageRequestRepository) {
    suspend operator fun invoke(id: Int, token: String) {
        repo.deleteRequest(id, token)
    }
}