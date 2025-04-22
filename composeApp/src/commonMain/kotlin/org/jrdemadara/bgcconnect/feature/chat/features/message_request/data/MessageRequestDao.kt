package org.jrdemadara.bgcconnect.feature.chat.features.message_request.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import org.jrdemadara.MessageRequest
import org.jrdemadara.MessageRequestQueries

class MessageRequestDao(private val queries: MessageRequestQueries) {

    fun getAllForRecipient(recipientId: Long): Flow<List<MessageRequest>> =
        queries.selectAll(recipientId).asFlow().mapToList(Dispatchers.Default)

    fun getPending(recipientId: Long): Flow<List<MessageRequest>> =
        queries.selectAllPending(recipientId).asFlow().mapToList(Dispatchers.Default)

    fun getRequest(id: Long): MessageRequest? =
        queries.selectRequest(id).executeAsOneOrNull()

     fun insert(request: MessageRequest) {
        queries.insertMessageRequest(
            id = request.id,
            senderId = request.senderId,
            firstname = request.firstname,
            lastname = request.lastname,
            avatar = request.avatar,
            recipientId = request.recipientId,
            status = request.status,
            requestedAt = request.requestedAt,
        )
    }

     fun updateStatus(id: Long, status: String) {
        queries.updateMessageRequestStatus(status, id)
    }

     fun clear() = queries.clearRequests()
}