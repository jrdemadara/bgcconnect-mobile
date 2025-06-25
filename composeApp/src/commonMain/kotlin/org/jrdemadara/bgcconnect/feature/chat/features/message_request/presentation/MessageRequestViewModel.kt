package org.jrdemadara.bgcconnect.feature.chat.features.message_request.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.jrdemadara.bgcconnect.core.local.SessionManager
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.data.MessageRequestDao
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.domain.AcceptMessageRequestUseCase
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.domain.DeclineMessageRequestUseCase
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.domain.SendMessageRequestUseCase

sealed class SendMessageRequestState {
    object Idle : SendMessageRequestState()
    object Loading : SendMessageRequestState()
    data class Success(val message: String) : SendMessageRequestState()
    data class Error(val error: String) : SendMessageRequestState()
}

sealed class AcceptMessageRequestState {
    object Idle : AcceptMessageRequestState()
    data class Loading(val requestId: Int) : AcceptMessageRequestState()
    data class Success(val message: String) : AcceptMessageRequestState()
    data class Error(val error: String) : AcceptMessageRequestState()
}

sealed class DeclineMessageRequestState {
    object Idle : DeclineMessageRequestState()
    data class Loading(val requestId: Int) : DeclineMessageRequestState()
    data class Success(val message: String) : DeclineMessageRequestState()
    data class Error(val error: String) : DeclineMessageRequestState()
}

@OptIn(FlowPreview::class)
class MessageRequestViewModel(
    private val sendMessageRequestUseCase: SendMessageRequestUseCase,
    private val acceptMessageRequestUseCase: AcceptMessageRequestUseCase,
    private val declineMessageRequestUseCase: DeclineMessageRequestUseCase,
    sessionManager: SessionManager,
    private val messageRequestDao: MessageRequestDao
) : ViewModel() {

    private val token = sessionManager.getToken()
    private val id = sessionManager.getUserId()

    private val _sendState = MutableStateFlow<SendMessageRequestState>(SendMessageRequestState.Idle)
    val sendState = _sendState.asStateFlow()

    private val _acceptState = MutableStateFlow<AcceptMessageRequestState>(AcceptMessageRequestState.Idle)
    val acceptState = _acceptState.asStateFlow()

    private val _declineState = MutableStateFlow<DeclineMessageRequestState>(DeclineMessageRequestState.Idle)
    val declineState = _declineState.asStateFlow()

    fun sendMessageRequest(recipientId: Int) {
        viewModelScope.launch {
            _sendState.value = SendMessageRequestState.Loading
            try {
                val result = sendMessageRequestUseCase(recipientId, token.toString())
                _sendState.value = SendMessageRequestState.Success(result)

            } catch (e: Exception) {
                _sendState.value = SendMessageRequestState.Error(e.message ?: "Unknown error")  // Handle any errors
            }
        }
    }

    val incomingRequests = messageRequestDao.getPending(id.toLong())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun acceptRequest(id: Int) {
        viewModelScope.launch {
            _acceptState.value = AcceptMessageRequestState.Loading(id)
            try {
                val result = acceptMessageRequestUseCase(id, token.toString())
                _acceptState.value = AcceptMessageRequestState.Success(result.toString())
            } catch (e: Exception) {
                _acceptState.value = AcceptMessageRequestState.Error(e.message ?: "Unknown error")  // Handle any errors
            }
        }
    }

    fun declineRequest(id: Int) {
        viewModelScope.launch {
            _declineState.value = DeclineMessageRequestState.Loading(id)
            try {
                val result = declineMessageRequestUseCase(id, token.toString())
                _declineState.value = DeclineMessageRequestState.Success(result.toString())
                messageRequestDao.updateStatus(id.toLong(), status = "declined")
            } catch (e: Exception) {
                _declineState.value = DeclineMessageRequestState.Error(e.message ?: "Unknown error")  // Handle any errors
            }
        }
    }
}