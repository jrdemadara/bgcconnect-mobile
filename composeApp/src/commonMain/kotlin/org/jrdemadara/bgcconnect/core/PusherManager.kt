package org.jrdemadara.bgcconnect.core

import kotlinx.coroutines.flow.Flow

expect class PusherManager() {
    fun disconnect()

    fun subscribeToUserChannel(userId: Long)
    fun subscribeToChatChannel(chatId: Long)
    fun subscribeToPresenceChannel(chatId: Long)

    fun getUserJoinedFlow(chatId: Long): Flow<Long>
    fun getUserLeftFlow(chatId: Long): Flow<Long>

    fun sendTypingEvent(chatId: Long)

    val messageRequests: Flow<String>
    val chatCreated: Flow<String>
    val chatReceived: Flow<String>
    val chatDelivered: Flow<String>
    val chatSeen: Flow<String>
    val typing: Flow<String>
    val userStatus: Flow<String>
}