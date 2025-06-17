package org.jrdemadara.bgcconnect.data

import kotlinx.serialization.Serializable

@Serializable
data class syncFCMRequest(
    val token: String,
)