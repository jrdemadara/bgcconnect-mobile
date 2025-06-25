package org.jrdemadara.bgcconnect.feature.login.presentation

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jrdemadara.bgcconnect.core.local.SessionManager
import org.jrdemadara.bgcconnect.core.pusher.PusherManager
import org.jrdemadara.bgcconnect.feature.login.domain.LoginUseCase
import org.koin.compose.getKoin

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val message: String) : LoginState()
    data class Error(val error: String) : LoginState()
}

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val sessionManager: SessionManager,
    private val pusherManager: PusherManager,
) : ViewModel() {

    private val _state = MutableStateFlow<LoginState>(LoginState.Idle)
    val state = _state.asStateFlow()

    fun login(phone: String, password: String) {
        viewModelScope.launch {
            _state.value = LoginState.Loading
            try {
                println("✅ Login FCM: ${sessionManager.getFCMToken().toString()}")
                val result = loginUseCase(phone, password, sessionManager.getFCMToken().toString())
                _state.value = LoginState.Success("Success!")
                sessionManager.saveSession(result.accessToken, result.data)
                pusherManager.authenticate()
                println("✅ Login success: ${result.data.id.toLong()}")
            } catch (e: Exception) {
                _state.value = LoginState.Error(e.message ?: "Unknown error")
            }
        }
    }
}