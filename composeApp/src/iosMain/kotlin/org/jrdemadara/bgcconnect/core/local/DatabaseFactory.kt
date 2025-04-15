package org.jrdemadara.bgcconnect.core.local

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import org.jrdemadara.LocalDatabase

class IOSDatabaseDriverFactory(): DatabaseDriverFactory {
    override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            LocalDatabase.Schema,
            "local.db"
        )
    }
}