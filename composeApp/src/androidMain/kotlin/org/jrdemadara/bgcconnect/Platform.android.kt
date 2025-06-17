package org.jrdemadara.bgcconnect

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import org.koin.core.scope.Scope
import java.util.UUID
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class AndroidPlatform(context: Context) : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT} - context:$context "
}

actual fun getPlatform(scope: Scope): Platform = AndroidPlatform(scope.get())

actual fun generateUUID(): String = UUID.randomUUID().toString()



actual object getCurrentTimestamp {
    @RequiresApi(Build.VERSION_CODES.O)
    actual fun nowIsoUtc(): String {
        val utcTime = Instant.now().atOffset(ZoneOffset.UTC)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")
        return formatter.format(utcTime)
    }
}