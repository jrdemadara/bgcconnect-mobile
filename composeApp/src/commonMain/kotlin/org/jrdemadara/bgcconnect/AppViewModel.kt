package org.jrdemadara.bgcconnect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.jrdemadara.bgcconnect.core.firebase.FirebaseEventManager
import org.jrdemadara.bgcconnect.core.pusher.PusherEventManager

class AppViewModel(
    private val pusherEventManager: PusherEventManager,
    private val firebaseEventManager: FirebaseEventManager,
) : ViewModel() {

    init {
        viewModelScope.launch {
            pusherEventManager.start()
            firebaseEventManager.start()
            firebaseEventManager.subscribeToTopic("draw")
            println("🚀 AppViewModel initialized, RealtimeEventManager started.")
        }
    }
}