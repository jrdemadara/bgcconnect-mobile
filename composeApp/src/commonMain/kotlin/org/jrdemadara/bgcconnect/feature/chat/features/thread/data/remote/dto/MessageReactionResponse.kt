package org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageReactionResponse(
    val remoteId: Int,
)