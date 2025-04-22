package org.jrdemadara.bgcconnect.core.local

import app.cash.sqldelight.db.SqlDriver
import org.jrdemadara.AppDatabase


expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

class DatabaseHelper(factory: DatabaseDriverFactory) {
    private val database = AppDatabase(factory.createDriver())
//    val messageRequestDao = MessageRequestDao(database.messageRequestQueries)
//    val userDao = UserDao(database.userQueries)
//    val chatDao = ChatDao(database.chatQueries)
//    val chatParticipantDao = ChatParticipantDao(database.chatParticipantsQueries)
//    val messageDao = MessageDao(database.messageQueries)
//    val messageStatusDao = MessageStatusDao(database.messageStatusQueries)
}