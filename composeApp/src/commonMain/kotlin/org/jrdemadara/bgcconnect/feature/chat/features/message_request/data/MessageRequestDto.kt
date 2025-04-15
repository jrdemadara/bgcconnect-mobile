package org.jrdemadara.bgcconnect.feature.chat.features.message_request.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Outgoing Request
@Serializable
data class MessageRequestData(
    val recipientId: Int,
)

// Incoming Request
@Serializable
data class IncomingRequest(
    val sender: Sender,
    val status: String,
    @SerialName("requested_at")
    val requestedAt: String
)

@Serializable
data class Sender(
    val id: Int,
    val firstname: String,
    val lastname: String,
    val avatar: String? = null
)