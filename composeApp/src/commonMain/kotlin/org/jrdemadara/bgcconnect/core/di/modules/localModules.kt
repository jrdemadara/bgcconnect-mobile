package org.jrdemadara.bgcconnect.core.di.modules

import org.jrdemadara.AppDatabase
import org.jrdemadara.bgcconnect.feature.chat.features.thread.data.local.MessageReactionDao
import org.jrdemadara.bgcconnect.feature.chat.features.thread.data.local.MessageReactionDaoImpl
import org.jrdemadara.bgcconnect.feature.chat.features.thread.data.local.MessageReactionRepositoryImpl
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.local.DeleteReactionUseCase
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.local.GetPendingReactionsUseCase
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.local.InsertReactionUseCase
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.local.MessageReactionRepository
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.local.ReactToMessageUseCase
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.local.UpdateReactionStatusUseCase
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val localModules = module {
    single<MessageReactionDao> { MessageReactionDaoImpl(get<AppDatabase>().messageReactionsQueries) }
    singleOf(::MessageReactionRepositoryImpl) { bind<MessageReactionRepository>() }
    single { InsertReactionUseCase(get()) }
    single { ReactToMessageUseCase(get()) }
    single { UpdateReactionStatusUseCase(get()) }
    single { DeleteReactionUseCase(get()) }
    single { GetPendingReactionsUseCase(get()) }
}