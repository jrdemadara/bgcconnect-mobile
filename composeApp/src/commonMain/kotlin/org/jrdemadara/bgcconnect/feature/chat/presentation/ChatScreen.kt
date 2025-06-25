package org.jrdemadara.bgcconnect.feature.chat.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bgcconnect.composeapp.generated.resources.Res
import bgcconnect.composeapp.generated.resources.u1
import coil3.compose.AsyncImage
import org.jrdemadara.bgcconnect.core.Routes
import org.jrdemadara.bgcconnect.ui.icons.HeroMagnifyingGlass
import org.jrdemadara.bgcconnect.ui.icons.HeroUserGroup
import org.jrdemadara.bgcconnect.ui.icons.HeroXMark
import org.jrdemadara.bgcconnect.util.capitalizeWords
import org.jrdemadara.bgcconnect.util.formatDate
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatScreen(navController: NavController, paddingValues: PaddingValues) {
    val chatViewModel = koinViewModel<ChatViewModel>()
    val chatState by chatViewModel.chatState.collectAsState()
    val chats = chatViewModel.chats.collectAsState(initial = emptyList()).value

    Column(  modifier = Modifier
        .padding(horizontal = 16.dp)
        .padding(paddingValues)
        .fillMaxSize())
    {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                IconButton(onClick = {
                    // Navigate to the Search screen
                    // navController.navigate(Routes.SEARCH)
                }) {
                    Icon(
                        imageVector = HeroMagnifyingGlass,
                        contentDescription = "Search Icon"
                    )
                }

                Text(
                    text = "Search",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    // Navigate to the Search screen
                    // navController.navigate(Routes.SEARCH)
                }) {
                    Icon(
                        imageVector = HeroUserGroup,
                        contentDescription = "User group icon"
                    )
                }

                Column {
                    Text(
                        text = "Create a group",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(text = "Stay on the same page with everyone", style = MaterialTheme.typography.titleSmall, color = Color.DarkGray)
                }
                IconButton(onClick = {
                    // Navigate to the Search screen
                    // navController.navigate(Routes.SEARCH)
                }) {
                    Icon(
                        imageVector = HeroXMark,
                        contentDescription = "Close"
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        if (chats.isNotEmpty()) {
            LazyColumn {
                items(chats) { chat ->
                    ChatItem(
                        chatId = chat.chatId,
                        userName = chat.fullName ?: "Unknown",
                        message = chat.lastMessage ?: "You can now start your conversation.",
                        isRead = chat.isRead!!,
                        date = chat.timestamp ?: "No date",
                        avatar = chat.avatar ?: Res.drawable.u1.toString(),
                        isOnline = chat.isOnline ?: false,
                        navController = navController
                    )
                }
            }
        } else {
            Text(text = "Say hi to someone! Start a conversation.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
        }
    }
}


@Composable
fun ChatItem(
    chatId: Long,
    userName: String,
    message: String,
    isRead: Boolean,
    date: String,
    avatar: String,
    isOnline: Boolean,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                navController.navigate("${Routes.THREAD}/$chatId")
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
        ) {
            AsyncImage(
                model = avatar,
                contentDescription = "User Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .clip(CircleShape)
            )

            // Green dot for online indicator
            if (isOnline){
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .align(Alignment.BottomEnd)
                        .background(Color.White, CircleShape) // optional border
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF4CAF50), CircleShape) // green color
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = userName.capitalizeWords(),
                fontWeight = if (!isRead) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = if (!isRead) FontWeight.Bold else FontWeight.Normal
            )
        }

        Text(
            text = formatDate(date) ,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}
