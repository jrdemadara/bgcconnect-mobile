package org.jrdemadara.bgcconnect.feature.chat.features.thread.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import org.jrdemadara.Message
import org.jrdemadara.User
import org.jrdemadara.bgcconnect.core.PusherManager
import org.jrdemadara.bgcconnect.core.local.SessionManager
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.ChatDao
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.MessageDao
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.MessageStatusDao
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.UserDao
import org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.TopBarData
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.SendMessageUseCase
import org.jrdemadara.bgcconnect.feature.chat.presentation.ChatState

sealed class SendChatState {
    object Idle : SendChatState()
    object Loading : SendChatState()
    data class Success(val message: String) : SendChatState()
    data class Error(val error: String) : SendChatState()
}

class ThreadViewModel(
    sessionManager: SessionManager,
    private val chatDao: ChatDao,
    private val userDao: UserDao,
    private val messageDao: MessageDao,
    private val messageStatusDao: MessageStatusDao,
    private val sendMessageUseCase: SendMessageUseCase,
    private val pusherManager: PusherManager
): ViewModel() {
    private val token = sessionManager.getToken()
    val id = sessionManager.getUserId()
    private val _sendChatState = MutableStateFlow<SendChatState>(
        SendChatState.Idle)
    val sendChatState = _sendChatState.asStateFlow()

     suspend fun sendChat(chatId: Int, content: String, messageType: String, replyTo: Int) {
        _sendChatState.value = SendChatState.Loading
        try {
            val result = sendMessageUseCase(chatId, content, messageType, replyTo, token.toString())
            _sendChatState.value = SendChatState.Success(result)
        } catch (e: Exception) {
            _sendChatState.value = SendChatState.Error(e.message ?: "Unknown error")
        }
    }

    private val _userId = MutableStateFlow<Long?>(null)

    val topBarData: StateFlow<TopBarData?> = _userId
        .filterNotNull()
        .flatMapLatest { userId ->
            userDao.getUserById(userId) // returns Flow<User?>
        }
        .map { user ->
            user?.let {
                TopBarData(
                    name = "${user.firstname} ${user.lastname}",
                    avatar = user.avatar,
                    isOnline = user.isOnline.toInt() == 1,
                    lastSeen = user.lastSeen.toString()
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun loadTopBarData(chatId: Long) {
        viewModelScope.launch {
            val otherUserId = chatDao.getOtherParticipantId(chatId, id.toLong())
            _userId.value = otherUserId
        }
    }

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    fun loadMessages(chatId: Long) {
        viewModelScope.launch {
            messageDao.getMessagesByChat(chatId)
                .collect { messageList ->
                    _messages.value = messageList
                }
        }
    }

    private val _typingState = MutableStateFlow(false)
    val typingState: StateFlow<Boolean> = _typingState


    fun observeTyping(chatId: Long) {
        viewModelScope.launch {
            pusherManager.typing.collect { rawJson ->
                val json = Json.parseToJsonElement(rawJson).jsonObject
                val userId = json["userId"]?.jsonPrimitive?.longOrNull
                if (userId != null && userId != id.toLong()) {
                    _typingState.value = true

                    // Clear typing after 3 seconds
                    delay(3000)
                    _typingState.value = false
                }
            }
        }
    }

    fun sendTyping(chatId: Long) {
        pusherManager.sendTypingEvent(chatId)
    }
}