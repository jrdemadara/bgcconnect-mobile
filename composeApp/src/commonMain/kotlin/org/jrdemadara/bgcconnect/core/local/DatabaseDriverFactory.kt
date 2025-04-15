package org.jrdemadara.bgcconnect.core.local

import app.cash.sqldelight.db.SqlDriver
import org.jrdemadara.AppDatabase
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.data.MessageRequestDao


expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

class DatabaseHelper(factory: DatabaseDriverFactory) {
    private val database = AppDatabase(factory.createDriver())
    val messageRequestDao = MessageRequestDao(database.messageRequestQueries)
}