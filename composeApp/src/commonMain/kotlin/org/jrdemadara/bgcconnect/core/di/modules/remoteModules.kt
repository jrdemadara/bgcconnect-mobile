package org.jrdemadara.bgcconnect.core.di.modules

import org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.api.MessageReactionApi
import org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.impl.MessageReactionRepositoryImpl
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.remote.repository.MessageReactionRepository
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.remote.usecases.SendMessageReactionUseCase
import org.jrdemadara.bgcconnect.feature.chat.features.thread.presentation.viewmodels.MessageReactionViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val remoteModules = module {
    single { MessageReactionApi(get()) }
    singleOf(::MessageReactionRepositoryImpl) { bind<MessageReactionRepository>() }
    single { SendMessageReactionUseCase(get()) }
    viewModel { MessageReactionViewModel(get(), get(), get(), get(), get(), get()) }

}