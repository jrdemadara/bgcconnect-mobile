package org.jrdemadara.bgcconnect.feature.chat.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import bgcconnect.composeapp.generated.resources.Res
import bgcconnect.composeapp.generated.resources.food
import bgcconnect.composeapp.generated.resources.profile
import bgcconnect.composeapp.generated.resources.u1
import bgcconnect.composeapp.generated.resources.u2
import bgcconnect.composeapp.generated.resources.u3
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jrdemadara.bgcconnect.core.Routes
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.presentation.IncomingMessageRequestState
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.presentation.MessageRequestState
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.presentation.MessageRequestViewModel
import org.jrdemadara.bgcconnect.ui.icons.HeroBell
import org.jrdemadara.bgcconnect.ui.icons.HeroMagnifyingGlass
import org.jrdemadara.bgcconnect.ui.icons.HeroUserGroup
import org.jrdemadara.bgcconnect.ui.icons.HeroXMark
import org.jrdemadara.bgcconnect.util.capitalizeWords
import org.jrdemadara.bgcconnect.util.formatDate
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatScreen(navController: NavController, paddingValues: PaddingValues) {
    val messageRequestViewModel = koinViewModel<MessageRequestViewModel>()
    val incomingState by messageRequestViewModel.incomingState.collectAsState()

    var hasMessageRequest by remember { mutableStateOf(false) }


    val chats = listOf(
        ChatData("Juan Dela Cruz", "Hey! Kamusta ka?", "10:30 AM", Res.drawable.u1, "unread"),
        ChatData("Maria Clara", "You: Meeting tayo later?", "9:45 AM", Res.drawable.u2, "read"),
        ChatData("Andres Bonifacio", "Ready na ang report!", "Yesterday", Res.drawable.u3, "read"),
        ChatData("Street Foodies", "Visit our new shop!", "Yesterday", Res.drawable.food, "read")
    )

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

        when (val state = incomingState) {
            is IncomingMessageRequestState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            is IncomingMessageRequestState.Success -> {
                if (state.requests.isNotEmpty()) {
                    Text(
                        text = "Message Requests",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyColumn {
                        items(state.requests) { request ->
                            MessageRequestItem(
                                fullName = "${request.sender.firstname} ${request.sender.lastname}",
                                date = formatDate(request.requestedAt),
                                avatarUrl = request.sender.avatar
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            is IncomingMessageRequestState.Error -> {
                Text("❌ ${state.message}", color = Color.Red)
            }

            IncomingMessageRequestState.Idle -> {
                // Optional: Show nothing or "Waiting for requests..."
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Chats",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(chats) { chat ->
                ChatItem(
                    userName = chat.name,
                    message = chat.message,
                    date = chat.date,
                    photoRes = chat.photoRes,
                    status = chat.status
                )
            }
        }
    }
}

@Composable
fun MessageRequestItem(
    fullName: String,
    date: String,
    avatarUrl: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "User Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = fullName.capitalizeWords(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "wants to connect with you",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }

        Text(
            text = date,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

@Composable
fun ChatItem(
    userName: String,
    message: String,
    date: String,
    photoRes: DrawableResource,
    status: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val painter = painterResource(photoRes)

        Image(
            painter = painter,
            contentDescription = "User Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = userName,
                fontWeight = if (status == "unread") FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = if (status == "unread") FontWeight.Bold else FontWeight.Normal
            )
        }

        Text(
            text = date,
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
    }
}

data class ChatData(
    val name: String,
    val message: String,
    val date: String,
    val photoRes: DrawableResource,
    val status: String
)