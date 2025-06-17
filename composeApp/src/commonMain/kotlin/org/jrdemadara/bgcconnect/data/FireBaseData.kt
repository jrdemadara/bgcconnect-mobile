package org.jrdemadara.bgcconnect.data

data class FirebaseMessageRequest(
    val title: String?,
    val body: String?,
    val data: FirebaseMessageRequestData
)

data class FirebaseMessageRequestData(
    val id: Int,
    val lastname: String,
    val firstname: String,
    val avatar: String,
    val status: String,
    val sender: Int,
    val requested_at: String,
)