package org.jrdemadara.bgcconnect

import org.koin.core.scope.Scope
import platform.UIKit.UIDevice
import platform.Foundation.NSDate
import platform.Foundation.NSUUID
import platform.Foundation.*

class IOSPlatform : Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(scope: Scope): Platform = IOSPlatform()

actual fun generateUUID(): String = NSUUID().UUIDString()



actual object getCurrentTimestamp {
    actual fun nowIsoUtc(): String {
        val formatter = NSDateFormatter()
        formatter.dateFormat = "yyyy-MM-dd'T'HH:mm:ssXXX"
        formatter.timeZone = NSTimeZone.timeZoneWithAbbreviation("UTC")!!
        return formatter.stringFromDate(NSDate())
    }
}