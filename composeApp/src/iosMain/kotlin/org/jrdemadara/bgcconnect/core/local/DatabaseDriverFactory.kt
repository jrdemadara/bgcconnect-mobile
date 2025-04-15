package org.jrdemadara.bgcconnect.core.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import org.jrdemadara.AppDatabase

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        val database = NativeSqliteDriver(AppDatabase.Schema, "app.db")
        return database
    }
}