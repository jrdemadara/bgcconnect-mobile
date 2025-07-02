package org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MessageReactionRequest(
    val messageId: Int,
    val userId: Int,
    val reaction: String,
)