package org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageReactionData(
    val id: Long,
    val messageId: Long,
    val userId: Long,
    val reaction: String,
)