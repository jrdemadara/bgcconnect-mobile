package org.jrdemadara.bgcconnect.feature.login.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val phone: String,
    val password: String,
)

@Serializable
data class LoginResponse(
    @SerialName("access_token") val accessToken: String,
    val data: LoginDto
)

@Serializable
data class LoginDto(
    val id: Int,
    val code: String,
    val phone: String,
    val points: Int,
    val level: Int,
    val firstname: String,
    val lastname: String,
    val middlename: String,
    val extension: String? = null,
    val avatar: String? = null
)