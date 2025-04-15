package org.jrdemadara.bgcconnect.feature.chat.features.message_request.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jrdemadara.bgcconnect.core.PusherManager
import org.jrdemadara.bgcconnect.core.local.SessionManager
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.data.IncomingRequest
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.data.MessageRequestDao
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.domain.MessageRequestUseCase

sealed class MessageRequestState {
    object Idle : MessageRequestState()
    object Loading : MessageRequestState()
    data class Success(val message: String) : MessageRequestState()
    data class Error(val error: String) : MessageRequestState()
}

@OptIn(FlowPreview::class)
class MessageRequestViewModel(
    private val messageRequestUseCase: MessageRequestUseCase,
    sessionManager: SessionManager,
    private val messageRequestDao: MessageRequestDao
) : ViewModel() {

    private val token = sessionManager.getToken()
    private val id = sessionManager.getUserId()

    private val _state = MutableStateFlow<MessageRequestState>(MessageRequestState.Idle)
    val state = _state.asStateFlow()

    // This directly observes the local DB for pending requests
    val incomingRequests = messageRequestDao.getPending(id.toLong())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun messageRequest(recipientId: Int) {
        viewModelScope.launch {
            _state.value = MessageRequestState.Loading
            try {
                val result = messageRequestUseCase(recipientId, token.toString())
                _state.value = MessageRequestState.Success(result)

            } catch (e: Exception) {
                _state.value = MessageRequestState.Error(e.message ?: "Unknown error")  // Handle any errors
            }
        }
    }
}