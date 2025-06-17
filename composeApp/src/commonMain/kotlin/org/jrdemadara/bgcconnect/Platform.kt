package org.jrdemadara.bgcconnect

import org.koin.core.scope.Scope

interface Platform {
    val name: String
}

expect fun getPlatform(scope: Scope): Platform

expect object getCurrentTimestamp {
    fun nowIsoUtc(): String
}

expect fun generateUUID(): String
