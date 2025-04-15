package org.jrdemadara.bgcconnect

import androidx.lifecycle.ViewModel
import org.jrdemadara.bgcconnect.core.RealtimeEventManager

class AppViewModel(
    private val realtimeEventManager: RealtimeEventManager
) : ViewModel() {

    init {
        realtimeEventManager.start()
        println("🚀 AppViewModel initialized, RealtimeEventManager started.")
    }
}