package org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Remote
@Serializable
data class SendChatRequest(
    val chatId: Int,
    val content: String,
    val messageType: String,
    val replyTo: Int,
)

@Serializable
data class MessageReceiveDto(
    val message: MessageDto,
    val status: MessageStatusDto
)

@Serializable
data class MessageDto(
    val id: Int,
    @SerialName("sender_id")
    val senderId: Long,
    @SerialName("chat_id")
    val chatId: Long,
    val content: String,
    @SerialName("message_type")
    val messageType: String,
    @SerialName("reply_to")
    val replyTo: Int? = null,
    @SerialName("created_at")
    val createAt: String,
    @SerialName("updated_at")
    val updatedAt: String,
)

@Serializable
data class MessageStatusDto(
    val id: Long,
    @SerialName("message_id")
    val messageId: Long,
    @SerialName("user_id")
    val userId: Long,
    val status: String,
    @SerialName("updated_at")
    val updatedAt: String
)

// Local

data class TopBarData(
    val name: String,
    val avatar: String?,
    val isOnline: Boolean,
    val lastSeen: String
)