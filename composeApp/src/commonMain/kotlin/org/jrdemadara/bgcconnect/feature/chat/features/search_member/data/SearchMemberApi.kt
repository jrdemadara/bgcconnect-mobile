package org.jrdemadara.bgcconnect.feature.chat.features.search_member.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.data.MemberDto
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.data.SearchMemberResponse

class SearchMemberApi(private val client: HttpClient) {
    suspend fun searchMember(key: String, token: String): List<MemberDto> {
        try {
            val response: SearchMemberResponse = client.get("/api/search-member") {
                header(HttpHeaders.Authorization, "Bearer $token")
                parameter("key", key)
            }.body()
            println("Response body: ${response.data}")
         //   val responseData: SearchMemberResponse = response.body()
            return response.data
        } catch (e: ClientRequestException) {
            println("Response Error: $e")
            // Handles 4xx errors
            when (e.response.status) {
                HttpStatusCode.Unauthorized -> {
                    throw Exception("Unauthorized: Invalid credentials.")
                }

                HttpStatusCode.UnprocessableEntity -> {
                    throw Exception("Unprocessable Entity: Invalid keyword or data format.")
                }

                else -> {
                    throw Exception("Client error: ${e.response.status}")
                }
            }
        } catch (e: ServerResponseException) {
            // Handles 5xx errors
            throw Exception("Server error: ${e.response.status}")
        } catch (e: Exception) {
            throw Exception("Unexpected error: ${e.message}")
        }
    }
}