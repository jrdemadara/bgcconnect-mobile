package org.jrdemadara.bgcconnect.feature.chat.features.message_request.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.composables.icons.lucide.Check
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Send
import kotlinx.coroutines.launch
import org.jrdemadara.bgcconnect.core.Routes
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.presentation.MessageRequestState
import org.jrdemadara.bgcconnect.feature.chat.features.message_request.presentation.MessageRequestViewModel
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.data.MemberDto
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.presentation.SearchMemberState
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.presentation.SearchMemberViewModel
import org.jrdemadara.bgcconnect.ui.components.CustomSearchField
import org.jrdemadara.bgcconnect.util.capitalizeWords

@Composable
fun MessageRequestScreen(navController: NavController, paddingValues: PaddingValues) {
    val messageRequestViewModel = koinViewModel<MessageRequestViewModel>()
    val incomingRequests by messageRequestViewModel.incomingRequests.collectAsState()



    val members by viewModel.members.collectAsState()
    val state by viewModel.state.collectAsState()
    val requestState by messageRequestViewModel.state.collectAsState()

    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val key = remember { mutableStateOf("") }
    val selectedMembers = remember { mutableStateMapOf<Int, MemberDto>() }

    val loadingMap = remember { mutableStateMapOf<Int, Boolean>() }
    val sentMap = remember { mutableStateMapOf<Int, Boolean>() }
    val errorMap = remember { mutableStateMapOf<Int, String?>() }
    var currentlySendingMemberId by remember { mutableStateOf<Int?>(null) }

    // Handle state changes from messageRequestViewModel
    LaunchedEffect(requestState) {
        val memberId = currentlySendingMemberId
        when (val state = requestState) {
            is MessageRequestState.Loading -> Unit
            is MessageRequestState.Success -> {
                memberId?.let {
                    loadingMap[it] = false
                    sentMap[it] = true
                    errorMap[it] = null
                }
                currentlySendingMemberId = null
            }

            is MessageRequestState.Error -> {
                memberId?.let {
                    loadingMap[it] = false
                    errorMap[it] = state.error
                }
                currentlySendingMemberId = null
            }

            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(paddingValues)
            .fillMaxSize()
    ) {

        CustomSearchField(
            label = "",
            value = key.value,
            onValueChange = {
                key.value = it
                viewModel.onSearchQueryChanged(it)
            },
            modifier = Modifier
                .padding(top = 5.dp)
                .focusRequester(focusRequester),
            keyboardActions = KeyboardActions(onNext = { keyboardController?.hide() }),
            enabled = true,
        )

        if (state is SearchMemberState.Loading) {
            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
        }

        if (state is SearchMemberState.Error) {
            Text("Error: ${(state as SearchMemberState.Error).error}")
        }

        if (state is SearchMemberState.Success) {
            if (members.isNotEmpty()) {
                LazyColumn(modifier = Modifier.padding(top = 5.dp)) {
                    items(members) { member ->
                        val isLoading = loadingMap[member.id] == true
                        val isSent = sentMap[member.id] == true
                        val error = errorMap[member.id]

                        MemberItem(
                            member = member,
                            isLoading = isLoading,
                            isSent = isSent,
                            error = error,
                            onClick = {
                                currentlySendingMemberId = member.id
                                loadingMap[member.id] = true
                                messageRequestViewModel.messageRequest(member.id)
                            }
                        )
                    }
                }
            } else {
                Text("No members found.")
            }
        }
    }
}

@Composable
fun MemberItem(
    member: MemberDto,
    isLoading: Boolean,
    isSent: Boolean,
    error: String?,
    onClick: (MemberDto) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = member.avatar,
            contentDescription = "User Photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${member.firstname.capitalizeWords()} ${member.lastname.capitalizeWords()}",
                style = MaterialTheme.typography.bodyLarge
            )

            error?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }

        Button(
            onClick = { onClick(member) },
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            enabled = !isLoading && !isSent
        ) {
            when {
                isLoading -> CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp)
                )

                isSent -> Icon(
                    imageVector = Lucide.Check,
                    contentDescription = "Sent",
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )

                else -> Icon(
                    imageVector = Lucide.Send,
                    contentDescription = "Send",
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )
            }
        }
    }
}
