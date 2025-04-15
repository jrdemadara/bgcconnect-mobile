package org.jrdemadara.bgcconnect.core

import kotlinx.coroutines.flow.Flow

expect class PusherManager() {
    fun disconnect()
    val messageRequests: Flow<String>
    val chatReceived: Flow<String>
    val chatDelivered: Flow<String>
    val chatSeen: Flow<String>
    val typing: Flow<String>
    val userStatus: Flow<String>
}