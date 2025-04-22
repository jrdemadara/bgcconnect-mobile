package org.jrdemadara.bgcconnect.feature.chat.features.message_request.data

import io.ktor.http.*
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

class MessageRequestApi(private val client: HttpClient) {

    suspend fun sendMessageRequest(recipientId: Int, token: String): String {
        val response: HttpResponse = client.post("/api/message-request") {
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(MessageRequestData(recipientId))
        }

        return when (response.status) {
            HttpStatusCode.Created -> {
                val responseBody: JsonObject = response.body()
                responseBody["message"]!!.jsonPrimitive.content
            }

            HttpStatusCode.Unauthorized -> {
                throw Exception("Unauthorized: Invalid credentials.")
            }

            HttpStatusCode.UnprocessableEntity -> {
                throw Exception("Unprocessable Entity: Invalid keyword or data format.")
            }

            HttpStatusCode.Conflict -> {
                val responseBody: JsonObject = response.body()
                throw Exception(responseBody["message"]!!.jsonPrimitive.content)
            }

            HttpStatusCode.Forbidden -> {
                val responseBody: JsonObject = response.body()
                throw Exception(responseBody["message"]!!.jsonPrimitive.content)
            }

            else -> {
                throw Exception("Unexpected error: ${response.status}")
            }
        }
    }

    suspend fun acceptMessageRequest(id: Int, token: String): String {
        val response: HttpResponse = client.patch("/api/message-request-accept") {
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(AcceptDeclineMessageRequestData(id))
        }

        return when (response.status) {
            HttpStatusCode.OK -> {
                "accepted"
            }

            HttpStatusCode.Unauthorized -> {
                throw Exception("Unauthorized: Invalid credentials.")
            }

            HttpStatusCode.UnprocessableEntity -> {
                throw Exception("Unprocessable Entity: Invalid keyword or data format.")
            }

            HttpStatusCode.NotFound -> {
                throw Exception("Not Found: Request not found.")
            }

            else -> {
                throw Exception("Unexpected error: ${response.status}")
            }
        }
    }


    suspend fun declineMessageRequest(id: Int, token: String): String {
        val response: HttpResponse = client.patch("/api/message-request-decline") {
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(AcceptDeclineMessageRequestData(id))
        }

        println(response.status)

        return when (response.status) {
            HttpStatusCode.OK -> {
                "declined"
            }

            HttpStatusCode.Unauthorized -> {
                throw Exception("Unauthorized: Invalid credentials.")
            }

            HttpStatusCode.UnprocessableEntity -> {
                throw Exception("Unprocessable Entity: Invalid keyword or data format.")
            }

            HttpStatusCode.NotFound -> {
                throw Exception("Not Found: Request not found.")
            }

            else -> {
                throw Exception("Unexpected error: ${response.status}")
            }
        }
    }
}