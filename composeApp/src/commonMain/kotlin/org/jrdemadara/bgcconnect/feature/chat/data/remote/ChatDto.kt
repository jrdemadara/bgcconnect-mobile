package org.jrdemadara.bgcconnect.feature.chat.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.data.Sender

// Remote Data


@Serializable
data class ChatCreatedDto(
    val chat: ChatDto,
    val participants: List<ParticipantDto>
)

@Serializable
data class ChatDto(
    val id: Long,
    @SerialName("chat_type")
    val chatType: String,
    val name: String? = null
)

@Serializable
data class ParticipantDto(
    val id: Long,
    val firstname: String,
    val lastname: String,
    val avatar: String? = null
)

