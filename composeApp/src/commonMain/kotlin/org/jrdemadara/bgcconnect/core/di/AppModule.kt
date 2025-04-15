package org.jrdemadara.bgcconnect.core.di

import com.russhwolf.settings.Settings
import org.jrdemadara.bgcconnect.AppViewModel
import org.jrdemadara.bgcconnect.core.PusherManager
import org.jrdemadara.bgcconnect.core.RealtimeEventManager
import org.jrdemadara.bgcconnect.core.local.SessionManager
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.data.MessageRequestApi
import org.jrdemadara.bgcconnect.feature.login.data.LoginApi
import org.jrdemadara.bgcconnect.feature.login.data.LoginRepositoryImpl
import org.jrdemadara.bgcconnect.feature.login.domain.LoginRepository
import org.jrdemadara.bgcconnect.feature.login.domain.LoginUseCase
import org.jrdemadara.bgcconnect.feature.login.presentation.LoginViewModel
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.data.SearchMemberApi
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.data.SearchMemberRepositoryImpl
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.data.MessageRequestRepositoryImpl
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.domain.MessageRequestRepository
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.domain.MessageRequestUseCase
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.presentation.MessageRequestViewModel
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.domain.SearchMemberRepository
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.domain.SearchMemberUseCase
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.presentation.SearchMemberViewModel
import org.jrdemadara.bgcconnect.getPlatform
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    viewModel { AppViewModel(get()) }
    factory { getPlatform(this) }
    single { Settings() }
    single { SessionManager(get()) }
    single { PusherManager() }
    single { RealtimeEventManager(get()) }

    singleOf(::LoginRepositoryImpl) { bind<LoginRepository>() }
    single { LoginApi(get()) }
    single<LoginRepository> { LoginRepositoryImpl(get()) }
    single { LoginUseCase(get()) }
    viewModel { LoginViewModel(get(), get()) }

    singleOf(::SearchMemberRepositoryImpl) { bind<SearchMemberRepository>() }
    single { SearchMemberApi(get()) }
    single<SearchMemberRepository> { SearchMemberRepositoryImpl(get()) }
    single { SearchMemberUseCase(get()) }
    viewModel { SearchMemberViewModel(get()) }

    singleOf(::MessageRequestRepositoryImpl) { bind<MessageRequestRepository>()}
    single { MessageRequestApi(get()) }
    single<MessageRequestRepository> { MessageRequestRepositoryImpl(get()) }
    single { MessageRequestUseCase(get()) }
    viewModel { MessageRequestViewModel(get(), get(), get()) }
}