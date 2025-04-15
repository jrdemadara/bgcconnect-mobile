package org.jrdemadara.bgcconnect.feature.chat.features.message_request.domain

import io.ktor.client.statement.HttpResponse

interface MessageRequestRepository {
    suspend fun messageRequest(recipientId: Int, token: String): String
}