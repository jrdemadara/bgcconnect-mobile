package org.jrdemadara.bgcconnect.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class SyncApi(private val client: HttpClient) {

    suspend fun syncFCM(token: String): HttpResponse {
        val response = client.post("/api/syncFCM") {
            contentType(ContentType.Application.Json)
            setBody(syncFCMRequest(token))
        }

        return when (response.status) {
            HttpStatusCode.OK -> {
                response.body<HttpResponse>()
            }

            HttpStatusCode.Unauthorized -> {
                throw Exception("Unauthorized")
            }

            HttpStatusCode.UnprocessableEntity -> {
                throw Exception("UnprocessableEntity")
            }

            else -> {
                throw Exception("Unexpected error: ${response.status}")
            }
        }
    }
}