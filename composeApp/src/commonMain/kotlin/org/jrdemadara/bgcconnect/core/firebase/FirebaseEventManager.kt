package org.jrdemadara.bgcconnect.core.firebase

import io.ktor.client.HttpClient
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jrdemadara.Chat
import org.jrdemadara.ChatParticipant
import org.jrdemadara.Message
import org.jrdemadara.MessageRequest
import org.jrdemadara.MessageStatus
import org.jrdemadara.User
import org.jrdemadara.bgcconnect.core.local.SessionManager
import org.jrdemadara.bgcconnect.core.pusher.PusherEventManager
import org.jrdemadara.bgcconnect.core.pusher.PusherManager
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.ChatDao
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.ChatParticipantDao
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.MessageDao
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.MessageStatusDao
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.UserDao
import org.jrdemadara.bgcconnect.feature.chat.data.remote.ChatCreatedDto
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.data.MessageRequestDao
import org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.MessageReceiveDto

class FirebaseEventManager(
    private val client: HttpClient,
    private val firebaseManager: FirebaseManager,
    private val sessionManager: SessionManager,
    private val pusherManager: PusherManager,
    private val pusherEventManager: PusherEventManager,
    private val messageRequestDao: MessageRequestDao,
    private val userDao: UserDao,
    private val chatDao: ChatDao,
    private val chatParticipantDao: ChatParticipantDao,
    private val messageDao: MessageDao,
    private val messageStatusDao: MessageStatusDao,
) {
    private val id = sessionManager.getUserId()
    private val now = Clock.System.now().toString()

    fun start(){
        firebaseManager.init()
        observeNewFcmToken()
        observeMessages()
    }

    private fun observeNewFcmToken(){
        println("🧐 Observing New FCM Token")
        CoroutineScope(Dispatchers.Default).launch {
            firebaseManager.tokenFlow.collect { fcmToken ->
                println("📡 New token: $fcmToken")
                sessionManager.saveFCMToken(fcmToken)
                try {
                    val response: HttpResponse = client.post("/api/update-fcm-token") {
                        header(HttpHeaders.Authorization, "Bearer ${sessionManager.getToken()}")
                        contentType(ContentType.Application.Json)
                        setBody(
                            buildJsonObject {
                                put("fcm_token", fcmToken)
                            }
                        )
                    }

                    when (response.status) {
                        HttpStatusCode.OK, HttpStatusCode.Created -> {
                            println("✅ FCM token updated successfully.")
                        }

                        HttpStatusCode.Unauthorized -> {
                            println("⛔ Unauthorized. Token may be expired.")
                        }

                        HttpStatusCode.NotFound -> {
                            println("⛔Not Found. User does not exist.")
                        }

                        else -> {
                            println("❌ Server error: ${response.status}")
                        }
                    }
                } catch (e: Exception) {
                    println("⚠️ Failed to upload FCM token: ${e.message}")
                }
            }
        }
    }

    private fun observeMessages(){
        println("🧐 Observing New Messages")
        CoroutineScope(Dispatchers.Default).launch {
            firebaseManager.messageFlow.collect { fcmMessage ->
                println("📥 Push received: $fcmMessage")
                when (val type = fcmMessage.data["type"]) {
                    "message-request" -> {
                        val request = MessageRequest(
                            id = fcmMessage.data["id"]?.toLong()!!,
                            senderId = fcmMessage.data["sender_id"]?.toLong()!!,
                            firstname = fcmMessage.data["firstname"]!!,
                            lastname = fcmMessage.data["lastname"]!!,
                            avatar = fcmMessage.data["avatar"],
                            recipientId = sessionManager.getUserId().toLong(),
                            status = "pending",
                            requestedAt = fcmMessage.data["requested_at"]!!,
                        )
                        messageRequestDao.insert(request)
                        println("📥 MessageRequest saved pushnotif: ${request.firstname}")
                    }
                    "chat-created" -> {
                        val dto = ChatCreatedDto(
                            chat = Json.decodeFromString(fcmMessage.data["chat"]!!),
                            participants = Json.decodeFromString(fcmMessage.data["participants"]!!),
                            messageRequestId = fcmMessage.data["message_request_id"]?.toLong()!!
                        )

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
                                id = 0,
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
                        messageRequestDao.updateStatus(dto.messageRequestId, status = "accepted")
                        pusherManager.subscribeToChatChannel(dto.chat.id)
                        pusherManager.subscribeToPresenceChannel(dto.chat.id)
                        pusherEventManager.observePresence(dto.chat.id)
                        println("📥 Chat Created: ${dto.chat.id}")
                    }
                    "chat-received" -> {
                        val dto = MessageReceiveDto(
                            message = Json.decodeFromString(fcmMessage.data["message"]!!),
                            status = Json.decodeFromString(fcmMessage.data["status"]!!),
                            localId = Json.decodeFromString(fcmMessage.data["local_id"]!!),
                        )
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
                    else -> {
                        println("⚠️ Unknown type: $type")
                    }
                }
            }
        }
    }

    fun subscribeToTopic(topic: String){
        firebaseManager.subscribeToTopic(topic)
    }
}