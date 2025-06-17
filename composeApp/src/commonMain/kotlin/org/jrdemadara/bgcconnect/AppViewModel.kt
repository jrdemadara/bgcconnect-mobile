package org.jrdemadara.bgcconnect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.jrdemadara.bgcconnect.core.firebase.FirebaseEventManager
import org.jrdemadara.bgcconnect.core.local.SessionManager
import org.jrdemadara.bgcconnect.core.pusher.RealtimeEventManager
import org.jrdemadara.bgcconnect.data.SyncApi

class AppViewModel(
    private val realtimeEventManager: RealtimeEventManager,
    private val sessionManager: SessionManager,
    private val syncApi: SyncApi,
    private val topicManager: FirebaseEventManager

) : ViewModel() {

    init {
        topicManager.subscribeToTopic("message-request")
        viewModelScope.launch {
            realtimeEventManager.start()
            println("🚀 AppViewModel initialized, RealtimeEventManager started.")
            println("🚀 FCM Token: ${sessionManager.getFCMToken()}")
        }
    }

    suspend fun syncFCMTokenToServer(){
        syncApi.syncFCM(sessionManager.getFCMToken().toString())
        println("🚀 FCM Token: ${sessionManager.getFCMToken()}")
    }
}