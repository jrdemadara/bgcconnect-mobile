package org.jrdemadara.bgcconnect.core

import android.util.Log
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PrivateChannel
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpChannelAuthorizer
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.lang.Exception

actual class PusherManager actual constructor() {
    private val settings: Settings = Settings()
    private val token = settings.getString("auth_token", defaultValue = "")
    private val userId = settings.getInt("id", -1).takeIf { it != -1 }?.toString()

    private var pusher: Pusher? = null

    private var privateChannel: PrivateChannel? = null

    private val _messageRequests = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 10
    )
    private val _chatReceived = MutableSharedFlow<String>()
    private val _chatDelivered = MutableSharedFlow<String>()
    private val _chatSeen = MutableSharedFlow<String>()
    private val _typing = MutableSharedFlow<String>()
    private val _userStatus = MutableSharedFlow<String>()

    actual val messageRequests: Flow<String>
        get() = _messageRequests.asSharedFlow()

    actual val chatReceived: Flow<String>
        get() = _chatReceived.asSharedFlow()

    actual val chatDelivered: Flow<String>
        get() = _chatDelivered.asSharedFlow()

    actual val chatSeen: Flow<String>
        get() = _chatSeen.asSharedFlow()

    actual val typing: Flow<String>
        get() = _typing.asSharedFlow()

    actual val userStatus: Flow<String>
        get() = _userStatus.asSharedFlow()

    init {
        userId?.let {
            connectToUserChannel()
        } ?: Log.e("Pusher", "❌ No userId found in Settings.")
    }

     private fun connectToUserChannel() {
        val authorizer = HttpChannelAuthorizer("http://192.168.1.6:8000/broadcasting/auth").apply {
            setHeaders(mapOf("Authorization" to "Bearer $token"))
        }

        val options = PusherOptions().apply {
            setCluster("ap1")
            setUseTLS(false)
            setChannelAuthorizer(authorizer)
        }

        pusher = Pusher("2832efc415d220033466", options)

        pusher?.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {
                Log.d("Pusher", "📡 Connected: ${change.currentState}")
            }

            override fun onError(message: String?, code: String?, e: Exception?) {
                Log.d("Pusher", "❌ Error: $message", e)
            }
        })


         privateChannel =  pusher!!.subscribePrivate(
             "private-user.$userId",
             object : PrivateChannelEventListener {
                 override fun onSubscriptionSucceeded(channelName: String?) {
                     Log.d("Pusher", "✅ Subscribed to: $channelName")

                     bindPrivateEvent("message-request", _messageRequests)
                     bindPrivateEvent("chat-received", _chatReceived)
                     bindPrivateEvent("chat-delivered", _chatDelivered)
                     bindPrivateEvent("chat-seen", _chatSeen)
                     bindPrivateEvent("typing", _typing)
                     bindPrivateEvent("user-status", _userStatus)
                 }

                 override fun onEvent(event: PusherEvent?) {
                     Log.d("Pusher", "✅ Event to: $event")
                 }

                 override fun onAuthenticationFailure(message: String?, e: Exception?) {
                     Log.d("Pusher", "❌ Auth failed: $message", e)
                 }
             }
         )
    }

    private fun bindPrivateEvent(eventName: String, flow: MutableSharedFlow<String>) {
        privateChannel?.bind(eventName, object : PrivateChannelEventListener {
            override fun onEvent(event: PusherEvent?) {
                Log.d("Pusher", "📥 [$eventName] raw data: ${event?.data}")
                event?.data?.let {
                    val emitted = flow.tryEmit(it)
                    Log.d("Pusher", "📤 [$eventName] emission status: $emitted")
                }
            }

            override fun onSubscriptionSucceeded(channelName: String?) {
                TODO("Not yet implemented")
            }

            override fun onAuthenticationFailure(message: String?, e: Exception?) {
                TODO("Not yet implemented")
            }


        })
    }

    actual fun disconnect() {
        pusher?.disconnect()
    }
}