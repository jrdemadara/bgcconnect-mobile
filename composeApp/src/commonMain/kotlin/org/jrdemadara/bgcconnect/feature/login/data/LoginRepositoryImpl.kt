package org.jrdemadara.bgcconnect.feature.login.data

import io.ktor.client.statement.*
import org.jrdemadara.bgcconnect.feature.login.domain.LoginRepository

class LoginRepositoryImpl(private val loginApi: LoginApi) : LoginRepository {
    override suspend fun login(phone: String, password: String): LoginResponse {
        return loginApi.loginUser(phone, password)
    }
}