package org.jrdemadara.bgcconnect.feature.chat.features.thread.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.CheckCheck
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Reply
import com.composables.icons.lucide.Send
import com.composables.icons.lucide.Trash
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jrdemadara.bgcconnect.ui.components.TopBarThread
import org.jrdemadara.bgcconnect.ui.components.TypingIndicator
import org.jrdemadara.bgcconnect.util.formatTimestamp
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ThreadScreen(navController: NavController, chatId: Int) {
    val scope = rememberCoroutineScope()
    val viewModel = koinViewModel<ThreadViewModel>()
    val topBarData by viewModel.topBarData.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.typingState.collectAsState(initial = false)
    val messageInput = remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val alreadyReadIds = remember { mutableStateListOf<Long>() }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val visibleMessageIds by remember {
        derivedStateOf {
            listState.layoutInfo.visibleItemsInfo.mapNotNull { info ->
                messages.getOrNull(info.index)?.messageId
            }
        }
    }

    LaunchedEffect(visibleMessageIds) {
        println(visibleMessageIds)
        val unreadVisible = visibleMessageIds.filter { msgId ->

            val msg = messages.find { it.messageId == msgId }
            println(msg)
            msg != null &&
                    msg.senderId != viewModel.id.toLong() &&
                    msg.status == "delivered" &&
                    !alreadyReadIds.contains(msgId)
        }

        if (unreadVisible.isNotEmpty()) {
            delay(300)
            unreadVisible.forEach { msgId ->
                println(msgId)
                viewModel.markMessageAsRead(
                    chatId = chatId,
                    messageId = msgId.toInt()
                ) { success ->
                    if (success) alreadyReadIds.add(msgId)
                }
            }
        }
    }

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

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                    ) {
                        Icon(
                            imageVector = Lucide.Reply,
                            contentDescription = "Reply",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(text = "Reply")
                    }
                    Column(
                    ) {
                        Icon(
                            imageVector = Lucide.Copy,
                            contentDescription = "Reply",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(text = "Copy")
                    }
                    Column(
                    ) {
                        Icon(
                            imageVector = Lucide.Trash,
                            contentDescription = "Reply",
                            modifier = Modifier.size(28.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(text = "Delete")
                    }
                }
                Button(onClick = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                }) {
                    Text("Hide bottom sheet")
                }
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            reverseLayout = true
        ) {
            if (isTyping) {
                item {
                    TypingIndicator(isTyping)
                }
            }

            items(messages) { message ->
//                MessageBubble(
//                    message = message.content,
//                    isMine = message.senderId == viewModel.id.toLong(),
//                    timestamp = formatTimestamp(message.createdAt),
//                    status = message.status,
//                    sendStatus = message.sendStatus
//                )

                val haptics = LocalHapticFeedback.current
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .combinedClickable(
                            onClick = {},
                            onLongClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                showBottomSheet = true
                            },
                            onLongClickLabel = "Context menu"
                        ),
                    horizontalAlignment = if ( message.senderId == viewModel.id.toLong()) Alignment.End else Alignment.Start
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = if ( message.senderId == viewModel.id.toLong()) Arrangement.End else Arrangement.Start
                    ) {
                        BoxWithConstraints {
                            val maxBubbleWidth = maxWidth * 0.8f

                            Box(
                                modifier = Modifier
                                    .widthIn(max = maxBubbleWidth)
                                    .wrapContentWidth()
                                    .background(
                                        color = if ( message.senderId == viewModel.id.toLong()) MaterialTheme.colorScheme.primary else Color(0xFFF0F0F0),
                                        shape = RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomEnd = if ( message.senderId == viewModel.id.toLong()) 0.dp else 16.dp,
                                            bottomStart = if ( message.senderId == viewModel.id.toLong()) 16.dp else 0.dp
                                        )
                                    )
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = message.content,
                                    color = if ( message.senderId == viewModel.id.toLong()) Color.White else Color.Black,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.padding(top = 2.dp, end = 8.dp, start = 8.dp)
                    ) {
                        Text(
                            text = formatTimestamp(message.createdAt),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )

                        if ( message.senderId == viewModel.id.toLong()) {
                            val (icon, tint) = when {
                                message.sendStatus == "failed" -> Lucide.CircleAlert to Color.Red
                                message.sendStatus == "sending" -> Lucide.Check to Color.Gray
                                message.sendStatus == "sent" && message.status == "delivered" -> Lucide.CheckCheck to Color.Gray
                                message.sendStatus == "sent" && message.status == "read" -> Lucide.CheckCheck to MaterialTheme.colorScheme.primary
                                else -> null to Color.Transparent
                            }

                            icon?.let {
                                Icon(
                                    imageVector = it,
                                    contentDescription = message.status,
                                    modifier = Modifier.size(16.dp),
                                    tint = tint
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }


}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessageBubble(
    message: String,
    isMine: Boolean,
    timestamp: String,
    status: String? = null,
    sendStatus: String
) {
    val haptics = LocalHapticFeedback.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                },
                onLongClickLabel = "Context menu"
            ),
        horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
        ) {
            BoxWithConstraints {
                val maxBubbleWidth = maxWidth * 0.8f

                Box(
                    modifier = Modifier
                        .widthIn(max = maxBubbleWidth)
                        .wrapContentWidth()
                        .background(
                            color = if (isMine) MaterialTheme.colorScheme.primary else Color(0xFFF0F0F0),
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomEnd = if (isMine) 0.dp else 16.dp,
                                bottomStart = if (isMine) 16.dp else 0.dp
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = message,
                        color = if (isMine) Color.White else Color.Black,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(top = 2.dp, end = 8.dp, start = 8.dp)
        ) {
            Text(
                text = timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray
            )

            if (isMine) {
                val (icon, tint) = when {
                    sendStatus == "failed" -> Lucide.CircleAlert to Color.Red
                    sendStatus == "sending" -> Lucide.Check to Color.Gray
                    sendStatus == "sent" && status == "delivered" -> Lucide.CheckCheck to Color.Gray
                    sendStatus == "sent" && status == "read" -> Lucide.CheckCheck to MaterialTheme.colorScheme.primary
                    else -> null to Color.Transparent
                }

                icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = status,
                        modifier = Modifier.size(16.dp),
                        tint = tint
                    )
                }
            }
        }
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