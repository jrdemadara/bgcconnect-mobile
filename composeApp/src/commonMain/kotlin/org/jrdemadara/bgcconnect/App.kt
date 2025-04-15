package org.jrdemadara.bgcconnect


import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jrdemadara.bgcconnect.core.AppNavHost
import org.jrdemadara.bgcconnect.ui.theme.AppTheme
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App() {
    KoinContext {
        AppTheme {
            val appViewModel: AppViewModel = koinViewModel()

            AppNavHost()
        }
    }
}

