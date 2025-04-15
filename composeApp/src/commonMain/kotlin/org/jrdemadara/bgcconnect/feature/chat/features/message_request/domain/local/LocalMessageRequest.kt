package org.jrdemadara.bgcconnect.feature.chat.features.message_request.domain.local

import kotlinx.serialization.Serializable

@Serializable
data class LocalMessageRequest(
    val id: Int,
    val senderId: Int,
    val recipientId: Int,
    val status: String,
    val timestamp: Int
)
