package org.jrdemadara.bgcconnect.feature.chat.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import bgcconnect.composeapp.generated.resources.food
import bgcconnect.composeapp.generated.resources.u1
import bgcconnect.composeapp.generated.resources.u2
import bgcconnect.composeapp.generated.resources.u3
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.presentation.MessageRequestViewModel
import org.jrdemadara.bgcconnect.ui.icons.HeroMagnifyingGlass
import org.jrdemadara.bgcconnect.ui.icons.HeroUserGroup
import org.jrdemadara.bgcconnect.ui.icons.HeroXMark
import org.jrdemadara.bgcconnect.util.capitalizeWords
import org.jrdemadara.bgcconnect.util.formatDate
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ChatScreen(navController: NavController, paddingValues: PaddingValues) {
    val messageRequestViewModel = koinViewModel<MessageRequestViewModel>()
    val incomingRequests by messageRequestViewModel.incomingRequests.collectAsState()

    Column(  modifier = Modifier
        .padding(horizontal = 16.dp)
        .padding(paddingValues)
        .fillMaxSize())
    {

        if (incomingRequests.isNotEmpty()) {
            Text(
                text = "Message Requests",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn {
                items(incomingRequests) { request ->
                    MessageRequestItem(
                        fullName = "${request.firstname} ${request.lastname}",
                        date = formatDate(request.requestedAt),
                        avatarUrl = request.avatar
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
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