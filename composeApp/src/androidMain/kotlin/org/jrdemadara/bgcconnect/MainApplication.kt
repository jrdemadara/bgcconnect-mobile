package org.jrdemadara.bgcconnect

import android.app.Application
import org.jrdemadara.bgcconnect.core.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
            //PushNotifications.stop()

        initKoin {
            androidContext(this@MainApplication)
            androidLogger()
        }
    }
}