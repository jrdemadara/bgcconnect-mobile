package org.jrdemadara.bgcconnect.core.di

import com.russhwolf.settings.Settings
import org.jrdemadara.AppDatabase
import org.jrdemadara.bgcconnect.AppViewModel
import org.jrdemadara.bgcconnect.core.PusherManager
import org.jrdemadara.bgcconnect.core.RealtimeEventManager
import org.jrdemadara.bgcconnect.core.local.DatabaseDriverFactory
import org.jrdemadara.bgcconnect.core.local.DatabaseHelper
import org.jrdemadara.bgcconnect.core.local.SessionManager
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.ChatDao
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.ChatParticipantDao
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.MessageDao
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.MessageStatusDao
import org.jrdemadara.bgcconnect.feature.chat.data.local.dao.UserDao
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.data.MessageRequestApi
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.data.MessageRequestDao
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.data.MessageRequestRepositoryImpl
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.domain.AcceptMessageRequestUseCase
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.domain.DeclineMessageRequestUseCase
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.domain.MessageRequestRepository
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.domain.SendMessageRequestUseCase
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.presentation.MessageRequestViewModel
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.data.SearchMemberApi
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.data.SearchMemberRepositoryImpl
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.domain.SearchMemberRepository
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.domain.SearchMemberUseCase
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.presentation.SearchMemberViewModel
import org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.ThreadApi
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.SendMessageUseCase
import org.jrdemadara.bgcconnect.feature.chat.features.thread.domain.ThreadRepository
import org.jrdemadara.bgcconnect.feature.chat.features.thread.presentation.ThreadViewModel
import org.jrdemadara.bgcconnect.feature.chat.presentation.ChatViewModel
import org.jrdemadara.bgcconnect.feature.login.data.LoginApi
import org.jrdemadara.bgcconnect.feature.login.data.LoginRepositoryImpl
import org.jrdemadara.bgcconnect.feature.chat.features.thread.data.remote.ThreadRepositoryImpl
import org.jrdemadara.bgcconnect.feature.login.domain.LoginRepository
import org.jrdemadara.bgcconnect.feature.login.domain.LoginUseCase
import org.jrdemadara.bgcconnect.feature.login.presentation.LoginViewModel
import org.jrdemadara.bgcconnect.getPlatform
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Platform-related
    factory { getPlatform(this) }

    // App-wide dependencies
    single { Settings() }
    single { SessionManager(get()) }

    // SQLDelight database setup
    //single { DatabaseDriverFactory(get()) } // ✅ Only required on Android. Safe here.
    single { AppDatabase(get<DatabaseDriverFactory>().createDriver()) }
    single<DatabaseHelper> { DatabaseHelper(get()) }

    // Real-time & messaging
    single { PusherManager() }
    single { RealtimeEventManager(get(), get(), get(), get(), get(), get(), get(), get()) }

    single { MessageRequestDao(get<AppDatabase>().messageRequestQueries) }
    single { UserDao(get<AppDatabase>().userQueries) }
    single { ChatDao(get<AppDatabase>().chatQueries, get<AppDatabase>().chatParticipantsQueries) }
    single { ChatParticipantDao(get<AppDatabase>().chatParticipantsQueries) }
    single { MessageDao(get<AppDatabase>().messageQueries) }
    single { MessageStatusDao(get<AppDatabase>().messageStatusQueries) }

    // Authentication & Login
    single { LoginApi(get()) }
    singleOf(::LoginRepositoryImpl) { bind<LoginRepository>() }
    single { LoginUseCase(get()) }
    viewModel { LoginViewModel(get(), get()) }

    // Search
    single { SearchMemberApi(get()) }
    singleOf(::SearchMemberRepositoryImpl) { bind<SearchMemberRepository>() }
    single { SearchMemberUseCase(get()) }
    viewModel { SearchMemberViewModel(get()) }

    // Message Requests
    single { MessageRequestApi(get()) }
    singleOf(::MessageRequestRepositoryImpl) { bind<MessageRequestRepository>() }
    single { SendMessageRequestUseCase(get()) }
    single { AcceptMessageRequestUseCase(get()) }
    single { DeclineMessageRequestUseCase(get()) }
    viewModel { MessageRequestViewModel(get(), get(), get(), get(), get()) }

    viewModel { ChatViewModel(get(), get(), get(), get()) }

    single { ThreadApi(get()) }
    singleOf(::ThreadRepositoryImpl) { bind<ThreadRepository>() }
    single { SendMessageUseCase(get()) }
    viewModel { ThreadViewModel(get(), get(), get(), get(), get(), get(), get()) }


    // App-level ViewModel
    viewModel { AppViewModel(get()) }
}