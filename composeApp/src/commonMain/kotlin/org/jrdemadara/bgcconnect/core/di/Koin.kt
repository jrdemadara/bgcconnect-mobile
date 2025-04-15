package org.jrdemadara.bgcconnect.core.di

import org.jrdemadara.bgcconnect.core.ktorModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.includes


fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        printLogger()
        includes(config)
        modules(ktorModule, appModule)
    }
}
