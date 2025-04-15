package org.jrdemadara.bgcconnect.feature.login.domain

import io.ktor.client.statement.*
import org.jrdemadara.bgcconnect.feature.login.data.LoginResponse

interface LoginRepository {
    suspend fun login(phone: String, password: String): LoginResponse
}