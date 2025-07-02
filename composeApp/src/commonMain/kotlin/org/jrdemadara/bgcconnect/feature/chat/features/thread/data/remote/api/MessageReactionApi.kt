package org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.dto.MessageReactionRequest
import org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.dto.MessageReactionResponse

class MessageReactionApi(private val client: HttpClient) {

    suspend fun send(messageId: Int, userId: Int, reaction: String, token: String): MessageReactionResponse {
        val response = client.post("/api/message-reaction") {
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(MessageReactionRequest(messageId ,userId, reaction))
        }

        return when (response.status) {
            HttpStatusCode.Created -> {
                response.body()
            }

            HttpStatusCode.Unauthorized -> {
                throw Exception("Unauthorized")
            }

            HttpStatusCode.UnprocessableEntity -> {
                throw Exception("Invalid Format")
            }

            else -> {
                throw Exception("Unexpected error: ${response.status}")
            }
        }
    }
}