package org.jrdemadara.bgcconnect.feature.login.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jrdemadara.bgcconnect.core.getBaseUrl

class LoginApi(private val client: HttpClient) {

    suspend fun loginUser(phone: String, password: String, fcmToken: String): LoginResponse {
        val response = client.post("/api/login") {
            setBody(LoginRequest(
                phone =  phone,
                password =  password,
                fcmToken =  fcmToken,
                deviceId = phone + password,
                deviceName = phone + password,
                loginLat = 14.599512,
                loginLon = 120.984222,
            ))
        }

        return when (response.status) {
            HttpStatusCode.OK -> {
                response.body<LoginResponse>()
            }

            HttpStatusCode.Unauthorized -> {
                val responseBody: JsonObject = response.body()
                val errorMessage = responseBody["error"]?.jsonPrimitive?.content
                    ?: "Wrong phone or password."
                throw Exception(errorMessage)
            }

            HttpStatusCode.UnprocessableEntity -> {
                throw Exception("Phone and password are required.")
            }

            else -> {
                throw Exception("Unexpected error: ${response.status}")
            }
        }
    }
}