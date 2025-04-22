package org.jrdemadara.bgcconnect.feature.chat.features.message_request.presentation

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import org.jrdemadara.bgcconnect.util.capitalizeWords
import org.jrdemadara.bgcconnect.util.formatDate
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MessageRequestScreen(navController: NavController, paddingValues: PaddingValues) {
    val viewModel = koinViewModel<MessageRequestViewModel>()
    val incomingRequests by viewModel.incomingRequests.collectAsState()
    val acceptState by viewModel.acceptState.collectAsState()
    val declineState by viewModel.declineState.collectAsState()

    Column(  modifier = Modifier
        .padding(horizontal = 16.dp)
        .padding(paddingValues)
        .fillMaxSize())
    {

        if (incomingRequests.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(incomingRequests) { request ->
                    MessageRequestItem(
                        fullName = "${request.firstname} ${request.lastname}",
                        date = formatDate(request.requestedAt),
                        avatarUrl = request.avatar,
                        onAccept = {
                            viewModel.acceptRequest(request.id.toInt())
                        },
                        onAcceptLoading = acceptState is AcceptMessageRequestState.Loading &&
                                (acceptState as? AcceptMessageRequestState.Loading)?.requestId == request.id.toInt(),
                        onDecline = {
                            viewModel.declineRequest(request.id.toInt())
                        },
                        onDeclineLoading = declineState is DeclineMessageRequestState.Loading &&
                                (declineState as? DeclineMessageRequestState.Loading)?.requestId == request.id.toInt(),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        } else {
            Text("At this moment, No one cares about you!")
        }
    }
}

@Composable
fun MessageRequestItem(
    fullName: String,
    date: String,
    avatarUrl: String?,
    onAccept: () -> Unit = {},
    onAcceptLoading: Boolean,
    onDecline: () -> Unit = {},
    onDeclineLoading: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // spacing between items
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = "User Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(82.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = fullName.capitalizeWords(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onAccept,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    if (onAcceptLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    }else{
                        Text("Accept")
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onDecline,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    if (onDeclineLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
                    }else{
                        Text("Decline", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}