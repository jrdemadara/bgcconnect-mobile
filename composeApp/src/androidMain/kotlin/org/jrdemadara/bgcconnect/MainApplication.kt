package org.jrdemadara.bgcconnect

import android.app.Application
import org.jrdemadara.bgcconnect.core.di.initKoin
import org.jrdemadara.bgcconnect.core.local.DatabaseDriverFactory
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.dsl.module

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MainApplication)
            androidLogger()
            modules (
                module {
                    single { DatabaseDriverFactory(applicationContext) }
                }
            )
        }
    }
}