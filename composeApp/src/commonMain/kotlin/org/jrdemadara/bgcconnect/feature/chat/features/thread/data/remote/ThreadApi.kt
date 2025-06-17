package org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

class ThreadApi(private val client: HttpClient) {

    suspend fun sendMessage(localId: Int, chatId: Int, content: String, messageType: String, replyTo: Int, token: String): String {
        val response = client.post("/api/chat-send") {
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(SendChatRequest(localId ,chatId, content, messageType, replyTo))
        }
        println(response)

        return when (response.status) {
            HttpStatusCode.Created -> {
                "success"
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

    suspend fun markMessagesAsRead(chatId: Int, messageId: Int, token: String): String {
        val response = client.patch("/api/chat-read") {
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(MarkMessagesReadRequest(chatId, messageId))
        }

        println(response)

        return when (response.status) {
            HttpStatusCode.OK -> {
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