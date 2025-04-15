package org.jrdemadara.bgcconnect.feature.chat.features.thread.presentation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.presentation.SearchMemberViewModel
import org.jrdemadara.bgcconnect.ui.components.TopBarThread
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ThreadScreen(navController: NavController, memberId: Int?) {
    val scope = rememberCoroutineScope()
    val viewModel = koinViewModel<SearchMemberViewModel>()
    val members by viewModel.members.collectAsState()
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = { TopBarThread(
            navController,
            image = "",
            name = "Johnny Roger",
            status = false
        ) }
    ) { paddingValues ->
        // Your actual content here
        Text("Thread for member ID: $memberId", modifier = Modifier.padding(paddingValues))
    }
}