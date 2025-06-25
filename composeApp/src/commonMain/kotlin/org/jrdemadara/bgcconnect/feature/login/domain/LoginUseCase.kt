package org.jrdemadara.bgcconnect.feature.login.domain

import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jrdemadara.bgcconnect.feature.login.data.LoginResponse

class LoginUseCase(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(phone: String, password: String, fcmToken: String): LoginResponse {
        return try {
            repository.login(phone, password, fcmToken)
        } catch (e: Exception) {
            throw Exception("Login failed: ${e.message}")
        }
    }
}