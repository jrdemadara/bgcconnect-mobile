package org.jrdemadara.bgcconnect

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.jrdemadara.bgcconnect.core.RealtimeEventManager

class AppViewModel(
    private val realtimeEventManager: RealtimeEventManager
) : ViewModel() {

    init {
        viewModelScope.launch {
            realtimeEventManager.start()
            println("🚀 AppViewModel initialized, RealtimeEventManager started.")
        }
    }
}