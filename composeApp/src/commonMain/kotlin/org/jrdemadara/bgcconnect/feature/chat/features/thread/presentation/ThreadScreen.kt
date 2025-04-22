package org.jrdemadara.bgcconnect.feature.chat.features.thread.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Send
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jrdemadara.bgcconnect.core.local.SessionManager
import org.jrdemadara.bgcconnect.ui.components.TopBarThread
import org.jrdemadara.bgcconnect.ui.components.WavyTypingText
import org.jrdemadara.bgcconnect.util.formatTimestamp
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ThreadScreen(navController: NavController, chatId: Int) {
    val scope = rememberCoroutineScope()
    val viewModel = koinViewModel<ThreadViewModel>()
    val topBarData by viewModel.topBarData.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.typingState.collectAsState(initial = false)
    val messageInput = remember { mutableStateOf("") }

    LaunchedEffect(chatId) {
        viewModel.loadTopBarData(chatId.toLong())
        viewModel.loadMessages(chatId.toLong())
        viewModel.observeTyping(chatId.toLong())
    }

    Scaffold(
        topBar = {
                TopBarThread(
                    navController,
                    avatar = topBarData?.avatar.toString(),
                    name = topBarData?.name.toString(),
                    isOnline = topBarData?.isOnline ?: false,
                    lastSeen = topBarData?.lastSeen.orEmpty()
                )

        },
        bottomBar = {
            MessageInputField(
                chatId = chatId.toLong(),
                message = messageInput.value,
                onMessageChange = { messageInput.value = it },
                onSend = {
                    if (messageInput.value.isNotBlank()) {
                        scope.launch {
                            viewModel.sendChat(
                                chatId,
                                messageInput.value,
                                "text",
                                0
                            )
                            messageInput.value = ""
                        }

                    }
                },
                onTyping = {
                    viewModel.sendTyping(chatId.toLong())
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            reverseLayout = true
        ) {
            if (isTyping) {
                item {
                    WavyTypingText()
                }
            }

            items(messages) { message ->
                MessageBubble(
                    message = message.content,
                    isMine = message.senderId == viewModel.id.toLong(),
                    timestamp = formatTimestamp(message.createdAt)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }


}

@Composable
fun MessageBubble(message: String, isMine: Boolean, timestamp: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isMine) MaterialTheme.colorScheme.primary else Color(0xFFF0F0F0),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message,
                color = if (isMine) Color.White else Color.Black,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Text(
            text = timestamp,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            modifier = Modifier.padding(top = 2.dp, end = 8.dp, start = 8.dp)
        )
    }
}

@Composable
fun MessageInputField(
    chatId: Long,
    message: String,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    onTyping: () -> Unit
) {
    val debounceJob = remember { mutableStateOf<Job?>(null) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = message,
            onValueChange = {
                onMessageChange(it)

                // Debounce typing event to avoid spamming
                debounceJob.value?.cancel()
                debounceJob.value = CoroutineScope(Dispatchers.Main).launch {
                    delay(300) // 👈 300ms debounce
                    onTyping()
                }
            },
            placeholder = { Text("Type a message...") },
            modifier = Modifier.weight(1f),
//            colors = TextFieldDefaults.textFieldColors(
//                containerColor = Color.Transparent,
//                focusedIndicatorColor = Color.Transparent,
//                unfocusedIndicatorColor = Color.Transparent
//            ),
            maxLines = 3
        )
        IconButton(onClick = onSend) {
            Icon(
                imageVector = Lucide.Send,
                contentDescription = "Send",
                modifier = Modifier.size(28.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}