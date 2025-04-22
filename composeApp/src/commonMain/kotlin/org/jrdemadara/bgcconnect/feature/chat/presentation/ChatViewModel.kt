package org.jrdemadara.bgcconnect.feature.chat.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.jrdemadara.bgcconnect.core.local.SessionManager
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.ChatDao
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.MessageDao
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.UserDao
import org.jrdemadara.bgcconnect.feature.chat.data.local.dto.ChatListItem

sealed class ChatState {
    object Idle : ChatState()
    object Loading : ChatState()
    data class Success(val message: String) : ChatState()
    data class Error(val error: String) : ChatState()
}

@OptIn(FlowPreview::class)
class ChatViewModel(
    sessionManager: SessionManager,
    private val chatDao: ChatDao,
    private val userDao: UserDao,
    private val messageDao: MessageDao,
) : ViewModel() {

    private val token = sessionManager.getToken() // remove if not needed
    private val id = sessionManager.getUserId()

    private val _chatState = MutableStateFlow<ChatState>(ChatState.Idle)
    val chatState = _chatState.asStateFlow()

    val chats: StateFlow<List<ChatListItem>> = chatDao.getAllChats(id.toLong())
        .flatMapLatest { chatList ->
            combine(chatList.map { chat ->
                val otherUserId = chatDao.getOtherParticipantId(chat.id, id.toLong())

                val userNameFlow = flowOf(userDao.getUserName(otherUserId)) // if static
                val avatarFlow = flowOf(userDao.getAvatar(otherUserId))     // if static
                val lastMessageFlow = flowOf(messageDao.getLastMessageForChat(chat.id)) // or make this a Flow
                val isOnlineFlow = userDao.getOnlineStatus(otherUserId)

                combine(userNameFlow, avatarFlow, lastMessageFlow, isOnlineFlow) { name, avatar, lastMessage, isOnline ->
                    ChatListItem(
                        chatId = chat.id,
                        fullName = name,
                        avatar = avatar,
                        lastMessage = lastMessage?.content,
                        timestamp = lastMessage?.createdAt,
                        isOnline = isOnline
                    )
                }
            }) { it.toList() }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
