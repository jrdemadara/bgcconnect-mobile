package org.jrdemadara.bgcconnect.core

import android.util.Log
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PresenceChannelEventListener
import com.pusher.client.channel.PrivateChannel
import com.pusher.client.channel.PrivateChannelEventListener
import com.pusher.client.channel.PusherEvent
import com.pusher.client.channel.User
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionStateChange
import com.pusher.client.util.HttpChannelAuthorizer
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
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

    private val _chatCreated = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 1
    )

    private val _chatReceived = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 1
    )

    private val userJoinedMap = mutableMapOf<Long, MutableSharedFlow<Long>>()
    private val userLeftMap = mutableMapOf<Long, MutableSharedFlow<Long>>()

    private val _chatDelivered = MutableSharedFlow<String>()
    private val _chatSeen = MutableSharedFlow<String>()

    private val _typing = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 10
    )

    private val _userStatus = MutableSharedFlow<String>()

//    actual val userJoined: Flow<String>
//        get() = _userJoined.asSharedFlow()
//
//    actual val userLeft: Flow<String>
//        get() = _userLeft.asSharedFlow()

    actual val messageRequests: Flow<String>
        get() = _messageRequests.asSharedFlow()

    actual val chatCreated: Flow<String>
        get() = _chatCreated.asSharedFlow()

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
    }

    actual fun subscribeToUserChannel(userId: Long){
        val channelName = "private-user.$userId"
        pusher?.subscribePrivate(channelName, object : PrivateChannelEventListener {
            override fun onEvent(event: PusherEvent?) {
                Log.d("Pusher", "✅Chat Event to: $event")
            }

            override fun onSubscriptionSucceeded(channelName: String?) {
                Log.d("Pusher", "✅ Subscribed to: $channelName")
            }

            override fun onAuthenticationFailure(message: String?, e: Exception?) {
                Log.d("Pusher", "❌ Auth failed: $message", e)
            }
        })

        // Bind all relevant user events
        bindUserEvent("message-request", _messageRequests)
        bindUserEvent("chat-created", _chatCreated)
    }

    actual fun subscribeToChatChannel(chatId: Long) {
        val channelName = "private-chat.$chatId"
        val channel = pusher?.subscribePrivate(channelName, object : PrivateChannelEventListener {
            override fun onEvent(event: PusherEvent?) {
                Log.d("Pusher", "📩 Generic Event from $channelName: ${event?.data}")
            }

            override fun onSubscriptionSucceeded(channelName: String?) {
                Log.d("Pusher", "✅ Subscribed to: $channelName")
            }

            override fun onAuthenticationFailure(message: String?, e: Exception?) {
                Log.e("Pusher", "❌ Auth failed for chat $chatId: $message", e)
            }
        })

        // Bind events on this specific chat channel instance
        channel?.let {
            bindChatEvent("chat-received", it, _chatReceived)
            bindChatEvent("chat-delivered", it, _chatDelivered)
            bindChatEvent("chat-seen", it, _chatSeen)
            bindChatEvent("client-typing", it, _typing)
        }
    }

    private fun bindUserEvent(eventName: String, flow: MutableSharedFlow<String>) {
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

    actual fun subscribeToPresenceChannel(chatId: Long) {
        val channelName = "presence-chat-presence.$chatId"
        val presenceChannel = pusher?.subscribePresence(channelName, object : PresenceChannelEventListener {

            override fun userSubscribed(channelName: String?, user: User?) {
                user?.id?.toLongOrNull()?.let {
                    userJoinedMap.getOrPut(chatId) { MutableSharedFlow(extraBufferCapacity = 5) }.tryEmit(it)
                    Log.d("Presence", "👤 User joined chat $chatId: $it")
                }
            }

            override fun userUnsubscribed(channelName: String?, user: User?) {
                user?.id?.toLongOrNull()?.let {
                    userLeftMap.getOrPut(chatId) { MutableSharedFlow(extraBufferCapacity = 5) }.tryEmit(it)
                    Log.d("Presence", "🚪 User left chat $chatId: $it")
                }
            }

            override fun onSubscriptionSucceeded(channelName: String?) {
                Log.d("Presence", "✅ Subscribed to presence channel: $channelName")
            }

            override fun onUsersInformationReceived(channelName: String?, users: MutableSet<User>?) {
                Log.d("Presence", "👥 Initial users in $channelName: $users")
                users?.forEach { user ->
                    user?.id?.toLongOrNull()?.let {
                        userJoinedMap.getOrPut(chatId) { MutableSharedFlow(extraBufferCapacity = 5) }.tryEmit(it)
                        Log.d("Presence", "👤 User joined chat $chatId: $it")
                    }
                }
            }

            override fun onAuthenticationFailure(message: String?, e: Exception?) {
                Log.e("Presence", "❌ Auth failed: $message", e)
            }

            override fun onEvent(event: PusherEvent?) {
                Log.d("Presence", "📥 Presence event: ${event?.data}")
            }
        })
    }

    actual fun getUserJoinedFlow(chatId: Long): Flow<Long> {
        return userJoinedMap.getOrPut(chatId) { MutableSharedFlow(extraBufferCapacity = 5) }
    }

    actual fun getUserLeftFlow(chatId: Long): Flow<Long> {
        return userLeftMap.getOrPut(chatId) { MutableSharedFlow(extraBufferCapacity = 5) }
    }

    private fun bindChatEvent(
        eventName: String,
        channel: PrivateChannel,
        flow: MutableSharedFlow<String>
    ) {
        channel.bind(eventName, object : PrivateChannelEventListener {
            override fun onEvent(event: PusherEvent?) {
                Log.d("Pusher", "📥 [$eventName] raw data: ${event?.data}")
                event?.data?.let {
                    val emitted = flow.tryEmit(it)
                    Log.d("Pusher", "📤 [$eventName] emission status: $emitted")
                }
            }

            override fun onSubscriptionSucceeded(channelName: String?) {}
            override fun onAuthenticationFailure(message: String?, e: Exception?) {}
        })
    }

    actual fun sendTypingEvent(chatId: Long) {
        val channelName = "private-chat.$chatId"
        val data = JSONObject().put("userId", userId).toString()

        pusher?.getPrivateChannel(channelName)?.trigger("client-typing", data)
    }

    actual fun disconnect() {
        pusher?.disconnect()
    }
}