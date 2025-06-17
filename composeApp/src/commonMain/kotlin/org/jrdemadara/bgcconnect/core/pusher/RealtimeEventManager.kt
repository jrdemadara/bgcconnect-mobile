package org.jrdemadara.bgcconnect.core.pusher

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import org.jrdemadara.Chat
import org.jrdemadara.ChatParticipant
import org.jrdemadara.Message
import org.jrdemadara.MessageRequest
import org.jrdemadara.MessageStatus
import org.jrdemadara.User
import org.jrdemadara.bgcconnect.core.local.SessionManager
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.ChatDao
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.ChatParticipantDao
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.MessageDao
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.MessageStatusDao
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.UserDao
import org.jrdemadara.bgcconnect.feature.chat.data.remote.ChatCreatedDto
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.data.IncomingRequest
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.data.MessageRequestDao
import org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.MessageRead
import org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.MessageReceiveDto

class RealtimeEventManager(
    private val pusherManager: PusherManager,
    private val sessionManager: SessionManager,
    private val messageRequestDao: MessageRequestDao,
    private val userDao: UserDao,
    private val chatDao: ChatDao,
    private val chatParticipantDao: ChatParticipantDao,
    private val messageDao: MessageDao,
    private val messageStatusDao: MessageStatusDao,
) {
    private val id = sessionManager.getUserId()
    private val now = Clock.System.now().toString()
    private val chatIds = chatDao.getAllChatIds()


    fun start() {
        chatIds.forEach { chatId ->
            pusherManager.subscribeToChatChannel(chatId)
            pusherManager.subscribeToPresenceChannel(chatId)
            observePresence(chatId)
        }

        observeMessageRequests()
        observeChatCreated()
        observeChatReceived()
        observeChatRead()
       // observeSubscribeUser()
        // Add more observers here if needed
    }

    private fun observeSubscribeUser() {
        println("🧐 Observing User Auth Token")
        CoroutineScope(Dispatchers.Default).launch {
            sessionManager.observeUserId().collect { userId ->
                println("User ID changed: $userId")
                pusherManager.subscribeToUserChannel(userId.toLong())
            }
        }
    }

    private fun observeMessageRequests() {
        println("🧐 Observing Message Request Event")
        CoroutineScope(Dispatchers.Default).launch {
            pusherManager.messageRequests.collect { rawJson ->
                try {
                    val dto = Json.decodeFromString<IncomingRequest>(rawJson)

                    // Map DTO to SQLDelight model
                    val request = MessageRequest(
                        id = dto.id.toLong(),
                        senderId = dto.sender.id.toLong(),
                        firstname = dto.sender.firstname,
                        lastname = dto.sender.lastname,
                        avatar = dto.sender.avatar,
                        recipientId = id.toLong(),
                        status = "pending",
                        requestedAt = dto.requestedAt
                    )
                    messageRequestDao.insert(request)
                    println("📥 MessageRequest saved: ${request.firstname}")
                } catch (e: Exception) {
                    println("❌ Failed to parse MessageRequest: ${e.message}")
                }
            }
        }
    }

    private fun observeChatCreated() {
        println("🧐 Observing Chat Created Event")
        CoroutineScope(Dispatchers.Default).launch {
            pusherManager.chatCreated.collect { rawJson ->
                try {
                    val dto = Json.decodeFromString<ChatCreatedDto>(rawJson)

                    dto.participants.forEach { userDto ->
                        if (userDto.id.toInt() != id) {
                            val user = User(
                                id = userDto.id,
                                firstname = userDto.firstname,
                                lastname = userDto.lastname,
                                avatar = userDto.avatar,
                                isOnline = 0,
                                lastSeen = ""
                            )
                            userDao.insert(user)
                        }

                        val participant = ChatParticipant(
                            id = 0, // Assuming auto-increment in DB
                            chatId = dto.chat.id,
                            userId = userDto.id,
                            role = null,
                            joinedAt = now,
                            createdAt = now,
                            updatedAt = now
                        )

                        chatParticipantDao.insertParticipant(participant)
                    }

                    val chat = Chat(
                        id = dto.chat.id,
                        userId = id.toLong(),
                        chatType = dto.chat.chatType,
                        name = dto.chat.name,
                        createdAt = now,
                        updatedAt = now,
                    )
                    chatDao.insertChat(chat)
                    pusherManager.subscribeToChatChannel(dto.chat.id)
                    pusherManager.subscribeToPresenceChannel(dto.chat.id)
                    observePresence(dto.chat.id)
                  println("📥 Chat Created: ${dto.chat.id}")
                } catch (e: Exception) {
                    println("❌ Failed to parse MessageRequest: ${e.message}")
                }
            }
        }
    }

    private fun observeChatReceived() {
        println("🧐 Observing Chat Received Event")
        CoroutineScope(Dispatchers.Default).launch {
            try {
                pusherManager.chatReceived.collect { rawJson ->
                    val dto = Json.decodeFromString<MessageReceiveDto>(rawJson)
                    val message = Message(
                        id = 0,
                        remoteId = dto.message.id.toLong(),
                        senderId = dto.message.senderId,
                        chatId = dto.message.chatId,
                        content = dto.message.content,
                        messageType = dto.message.messageType,
                        replyTo = dto.message.replyTo?.toLong(),
                        sendStatus = "received",
                        createdAt = dto.message.createdAt,
                        updatedAt = dto.message.updatedAt
                    )

                    val isExists = messageDao.isIdExists(dto.localId.toLong())

                    if (isExists){
                        messageDao.updateMessageSendStatusSent(
                            messageId = dto.localId.toLong(),
                            remoteId = dto.message.id.toLong(),
                            status = "sent",
                            createdAt = dto.message.createdAt,
                            updatedAt = dto.message.updatedAt
                        )
                    }else {
                        messageDao.insertMessage(message)
                    }

                    val messageStatus = MessageStatus(
                        id = dto.status.id,
                        messageId = dto.status.messageId,
                        userId = dto.status.userId,
                        status = dto.status.status,
                        updatedAt = dto.status.updatedAt
                    )

                    messageStatusDao.insertStatus(messageStatus)

                    println("📥 Message Saved: ${dto.message.id}")
                    println("📥 Message Status Saved: ${dto.status.id}")
                }

            } catch (e: Exception) {
                println("❌ Failed to parse ChatReceived: ${e.message}")
            }
        }
    }

    private fun observePresence(chatId: Long) {
        println("🧐 Observing Presence Channel")
        CoroutineScope(Dispatchers.Default).launch {
            pusherManager.getUserJoinedFlow(chatId).collect { userId ->
                userDao.updateOnlineStatus(userId, true)
            }
        }

        CoroutineScope(Dispatchers.Default).launch {
            pusherManager.getUserLeftFlow(chatId).collect { userId ->
                userDao.updateOnlineStatus(userId, false)
                userDao.updateLastSeen(userId, now)
            }
        }
    }

    private fun observeChatRead() {
        println("🧐 Observing Chat Read Event")
        CoroutineScope(Dispatchers.Default).launch {
            pusherManager.chatRead.collect { rawJson ->
                try {
                    val data = Json.decodeFromString<MessageRead>(rawJson)
                    println("✅ Message Read Event: $data")

                    data.messageIds.forEach { messageId ->
                        messageStatusDao.markMessagesAsRead(
                            updatedAt = now,
                            messageId = messageId.toInt(),
                            userId = data.readerId.toInt()
                        )
                    }

                } catch (e: Exception) {
                    println("❌ Failed to parse MessageReadPayload: ${e.message}")
                }
            }
        }
    }
}