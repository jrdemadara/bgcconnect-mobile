package org.jrdemadara.bgcconnect.feature.chat.features.thread.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import bgcconnect.composeapp.generated.resources.Res
import bgcconnect.composeapp.generated.resources.one_heart
import com.composables.icons.lucide.Camera
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.CheckCheck
import com.composables.icons.lucide.ChevronRight
import com.composables.icons.lucide.CircleAlert
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.Image
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Mic
import com.composables.icons.lucide.Reply
import com.composables.icons.lucide.SendHorizontal
import com.composables.icons.lucide.Trash
import com.composables.icons.lucide.X
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jrdemadara.bgcconnect.feature.chat.features.thread.presentation.viewmodels.MessageReactionViewModel
import org.jrdemadara.bgcconnect.feature.chat.features.thread.presentation.viewmodels.ThreadViewModel
import org.jrdemadara.bgcconnect.ui.components.TopBarThread
import org.jrdemadara.bgcconnect.ui.components.TypingIndicator
import org.jrdemadara.bgcconnect.util.formatTimestamp
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ThreadScreen(navController: NavController, chatId: Int) {
    val scope = rememberCoroutineScope()
    val viewModel = koinViewModel<ThreadViewModel>()
    val viewModelMessageReaction = koinViewModel<MessageReactionViewModel>()
    val topBarData by viewModel.topBarData.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val isTyping by viewModel.typingState.collectAsState(initial = false)
    val messageInput = remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val alreadyReadIds = remember { mutableStateListOf<Long>() }

    var isReply by remember { mutableStateOf(false) }
    var selectedMessage by remember { mutableStateOf(-1L) }
    var selectedMessageRemoteId by remember { mutableStateOf(-1L) }
    var selectedMessageContent by remember { mutableStateOf("") }

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
        delay(500)

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
                message = messageInput.value,
                onMessageChange = { messageInput.value = it },
                onSend = {
                    if (messageInput.value.isNotBlank()) {
                        scope.launch {
                            viewModel.sendChat(
                                chatId,
                                messageInput.value,
                                "text",
                                replyTo = if (selectedMessageRemoteId != -1L) selectedMessageRemoteId.toInt() else null
                            )
                            messageInput.value = ""
                            isReply = false
                            selectedMessage = -1L
                            selectedMessageRemoteId = -1L
                            selectedMessageContent = ""
                        }

                    }
                },
                onTyping = {
                    viewModel.sendTyping(chatId.toLong())
                },
                isReply = isReply,
                onCancelReply = {
                    isReply = false
                    selectedMessage = -1L
                    selectedMessageRemoteId = -1L
                    selectedMessageContent = ""
                },
                selectedMessageContent = selectedMessageContent
            )
        }
    ) { paddingValues ->

        val emojis = listOf("one-heart", "❤️", "😂", "😮", "😢")

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    emojis.forEach { emoji ->
                        if (emoji == "one-heart"){
                            Image(
                                painterResource(Res.drawable.one_heart),
                                contentDescription = "Logo",
                                modifier = Modifier
                                    .size(36.dp)
                                    .clickable {
                                        viewModelMessageReaction.onReact(
                                            messageId = selectedMessage,
                                            userId = viewModel.id.toLong(),
                                            reaction = emoji
                                        )
                                        showBottomSheet = false
                                    println("Emoji react $emoji to: ${selectedMessage}")
                                },
                            )
                        }else {
                            Text(
                                text = emoji,
                                fontSize = 38.sp,
                                modifier = Modifier
                                    .clickable {
                                        // handle emoji reaction
                                        viewModelMessageReaction.onReact(
                                            messageId = selectedMessage,
                                            userId = viewModel.id.toLong(),
                                            reaction = emoji
                                        )
                                        showBottomSheet = false
                                        println("Emoji react $emoji to: ${selectedMessage}")
                                    }
                                    .padding(4.dp)
                            )
                        }

                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 22.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.clickable{
                            println("Reply to $selectedMessage")
                            isReply = true
                            scope.launch { sheetState.hide() }.invokeOnCompletion {
                                if (!sheetState.isVisible) {
                                    showBottomSheet = false
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Lucide.Reply,
                            contentDescription = "Reply",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(text = "Reply")
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Copy,
                            contentDescription = "Copy",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(text = "Copy")
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Lucide.Trash,
                            contentDescription = "Trash",
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(text = "Delete")
                    }
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
                                selectedMessage = message.messageId
                                selectedMessageRemoteId = message.remoteId!!
                                selectedMessageContent = message.content
                                println("Selected message: ${message.content}")
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
                                Column {
                                    message.replyContent?.let {
                                        Box(
                                            modifier = Modifier
                                                .padding(bottom = 4.dp)
                                                .background(
                                                    color = if (message.senderId == viewModel.id.toLong())
                                                        Color(0xFFF0F0F0).copy(alpha = 0.2f)
                                                    else
                                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                                    shape = RoundedCornerShape(8.dp)
                                                )
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                                .wrapContentWidth()
                                        ) {
                                            Text(
                                                text = it,
                                                color = if (message.senderId == viewModel.id.toLong())
                                                    Color.White
                                                else
                                                    Color.DarkGray,
                                                style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    }

                                    Text(
                                        text = message.content,
                                        color = if (message.senderId == viewModel.id.toLong())
                                            Color.White else Color.Black,
                                        style = MaterialTheme.typography.bodyLarge
                                    )

                                    // Reactions (add this block below the content)
                                    if (!message.reactions.isNullOrEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Row(
                                            modifier = Modifier
                                                .wrapContentWidth()
                                                .background(
                                                    color = if (message.senderId == viewModel.id.toLong())
                                                        Color.White.copy(alpha = 0.1f)
                                                    else
                                                        Color.LightGray.copy(alpha = 0.3f),
                                                    shape = RoundedCornerShape(12.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            message.reactions?.split(",")?.forEach { reaction ->
                                                println("this is my reaction $reaction")
                                                if (reaction == "one-heart") {
                                                    Image(
                                                        painter = painterResource(Res.drawable.one_heart),
                                                        contentDescription = "One Heart",
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                } else {
                                                    Text(
                                                        text = reaction,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        fontSize = 18.sp,
                                                        color = if (message.senderId == viewModel.id.toLong()) Color.White else Color.Black
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
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


@Composable
fun MessageInputField(
    message: String,
    selectedMessageContent: String,
    isReply: Boolean,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    onTyping: () -> Unit,
    onCancelReply: () -> Unit
) {
    val debounceJob = remember { mutableStateOf<Job?>(null) }
    val isReplyState = rememberUpdatedState(newValue = isReply)

    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 6.dp, end = 6.dp, top = 6.dp)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        if (isReplyState.value) {
            Box(
                modifier = Modifier.padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                                append("Replying to: ")
                            }
                            withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                                append(selectedMessageContent)
                            }
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = onCancelReply,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.X,
                            contentDescription = "Cancel Reply",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .padding(start = 6.dp, end = 6.dp, bottom = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                enter = fadeIn(animationSpec = tween(durationMillis = 50)),
                exit = fadeOut(animationSpec = tween(durationMillis = 50)),
                visible = !imeVisible)
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.wrapContentWidth()
                ) {
                    IconButton(
                        onClick = { /* TODO: Add camera action */ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Camera,
                            contentDescription = "Camera Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(
                        onClick = { /* TODO: Add gallery action */ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Image,
                            contentDescription = "Gallery Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(
                        onClick = { /* TODO: Add mic action */ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Lucide.Mic,
                            contentDescription = "Mic Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            if (imeVisible) {
                IconButton(
                    onClick = {
                        focusManager.clearFocus()
                    }
                ) {
                    Icon(
                        imageVector = Lucide.ChevronRight,
                        contentDescription = "Collapse",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(4.dp))

            TextField(
                value = message,
                onValueChange = {
                    onMessageChange(it)
                    debounceJob.value?.cancel()
                    debounceJob.value = CoroutineScope(Dispatchers.Main).launch {
                        delay(300)
                        onTyping()
                    }
                },
                placeholder = { Text("Message", fontSize = 16.sp) },
                modifier = Modifier
                    .height(52.dp)
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .focusRequester(focusRequester),
                interactionSource = interactionSource,
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    disabledContainerColor = MaterialTheme.colorScheme.surface,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                maxLines = 4
            )

            IconButton(onClick = onSend) {
                Icon(
                    imageVector = Lucide.SendHorizontal,
                    contentDescription = "Send",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}