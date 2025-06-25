package org.jrdemadara.bgcconnect.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import bgcconnect.composeapp.generated.resources.Res
import bgcconnect.composeapp.generated.resources.logo
import coil3.compose.AsyncImage
import com.composables.icons.lucide.*
import org.jetbrains.compose.resources.painterResource
import org.jrdemadara.bgcconnect.core.Routes
import org.jrdemadara.bgcconnect.ui.icons.HeroBell
import org.jrdemadara.bgcconnect.ui.icons.HeroUser
import org.jrdemadara.bgcconnect.util.capitalizeWords
import org.jrdemadara.bgcconnect.util.currentRoute
import org.jrdemadara.bgcconnect.util.formatTimestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarThread(
    navController: NavController,
    avatar: String,
    name: String,
    isOnline: Boolean,
    lastSeen: String) {
    TopAppBar(
        title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = avatar,
                        contentDescription = "User Photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column(
                        verticalArrangement = Arrangement.spacedBy((-7).dp)) {
                        Text(
                            text = name.capitalizeWords(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isOnline){
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(
                                            color = Color(0xFF4CAF50),
                                            CircleShape) // green color
                                )
                            }

                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = when {
                                    isOnline -> "Online"
                                    lastSeen.isNotEmpty() -> "last seen ${formatTimestamp(lastSeen)}"
                                    else -> "Inactive"
                                },
                                fontSize = 14.sp,
                                color = when {
                                    isOnline -> Color(0xFF4CAF50) // Green
                                    lastSeen.isNotEmpty() -> Color.Gray
                                    else -> Color(0xFFF44336) // Red
                                }
                            )
                        }

                    }
                }

        },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Lucide.ArrowLeft,
                    contentDescription = "Back",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant // Dynamic icon color
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface, // Adaptable background
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        actions = {
            IconButton(onClick = {
                // Handle other action
            }) {
                Icon(
                    imageVector = Lucide.Phone,
                    contentDescription = "Voice Call",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = {
                // Handle other action
            }) {
                Icon(
                    imageVector = Lucide.Video,
                    contentDescription = "Video Call",
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}

