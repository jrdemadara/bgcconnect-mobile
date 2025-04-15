package org.jrdemadara.bgcconnect.feature.chat.features.message_request.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jrdemadara.bgcconnect.core.PusherManager
import org.jrdemadara.bgcconnect.core.local.SessionManager
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.data.IncomingRequest
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.domain.MessageRequestUseCase

sealed class MessageRequestState {
    object Idle : MessageRequestState()
    object Loading : MessageRequestState()
    data class Success(val message: String) : MessageRequestState()
    data class Error(val error: String) : MessageRequestState()
}

sealed class IncomingMessageRequestState {
    object Idle : IncomingMessageRequestState()
    object Loading : IncomingMessageRequestState()
    data class Success(val requests: List<IncomingRequest>) : IncomingMessageRequestState()
    data class Error(val message: String) : IncomingMessageRequestState()
}

@OptIn(FlowPreview::class)
class MessageRequestViewModel(
    private val messageRequestUseCase: MessageRequestUseCase,
    sessionManager: SessionManager,
    private val pusherManager: PusherManager
) : ViewModel() {

    private val token = sessionManager.getToken()

    private val _state = MutableStateFlow<MessageRequestState>(MessageRequestState.Idle)
    val state = _state.asStateFlow()

    // For receiving multiple incoming requests
    private val _incomingState = MutableStateFlow<IncomingMessageRequestState>(IncomingMessageRequestState.Idle)
    val incomingState = _incomingState.asStateFlow()

    private val collectedRequests = mutableListOf<IncomingRequest>()

    init {
        listenForIncomingRequests()
    }

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


    private fun listenForIncomingRequests() {
        _incomingState.value = IncomingMessageRequestState.Loading

        viewModelScope.launch {
            pusherManager.messageRequests.collect { rawJson ->
                println("📥 Raw JSON: $rawJson")
                try {
                    val request = Json.decodeFromString<IncomingRequest>(rawJson)
                    println("📩 Parsed Request: $request")

                    collectedRequests.add(request)
                    _incomingState.value = IncomingMessageRequestState.Success(collectedRequests.toList())
                } catch (e: Exception) {
                    _incomingState.value = IncomingMessageRequestState.Error("Invalid message request format")
                    println("❌ Error parsing request: ${e.message}")
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        pusherManager.disconnect()
    }
}