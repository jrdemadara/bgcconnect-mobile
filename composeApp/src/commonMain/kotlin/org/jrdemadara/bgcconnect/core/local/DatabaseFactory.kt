package org.jrdemadara.bgcconnect.core.local

import app.cash.sqldelight.db.SqlDriver
import org.jrdemadara.LocalDatabase
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.domain.local.LocalMessageRequest
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

class LocalDatabase(
    databaseDriverFactory: DatabaseDriverFactory
){
    private val database = LocalDatabase(
        databaseDriverFactory.createDriver()
    )

    private val query = database.localDatabaseQueries
    @OptIn(ExperimentalTime::class)
   // val timestamp = Clock.System.now().toEpochMilliseconds()
    fun insertMessageRequest(messageRequest: LocalMessageRequest) {
        query.transaction {
            query.insertMessageRequest(
                senderId = messageRequest.senderId.toLong(),
                recipientId = messageRequest.recipientId.toLong(),
                status = messageRequest.status,
                timestamp = 1
            )
        }
    }

}