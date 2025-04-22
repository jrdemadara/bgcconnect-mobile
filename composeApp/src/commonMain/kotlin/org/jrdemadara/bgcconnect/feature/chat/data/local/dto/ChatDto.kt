package org.jrdemadara.bgcconnect.feature.chat.data.local.dto

data class ChatListItem(
    val chatId: Long,
    val fullName: String,
    val avatar: String?,
    val lastMessage: String?,
    val timestamp: String?,
    val isOnline: Boolean?
)
