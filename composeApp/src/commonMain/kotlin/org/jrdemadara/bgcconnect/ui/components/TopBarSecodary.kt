package org.jrdemadara.bgcconnect.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import bgcconnect.composeapp.generated.resources.Res
import bgcconnect.composeapp.generated.resources.logo
import com.composables.icons.lucide.*
import org.jetbrains.compose.resources.painterResource
import org.jrdemadara.bgcconnect.core.Routes
import org.jrdemadara.bgcconnect.ui.icons.HeroBell
import org.jrdemadara.bgcconnect.ui.icons.HeroUser
import org.jrdemadara.bgcconnect.util.currentRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarSecondary(navController: NavController) {
    val currentRoute = currentRoute(navController)

    val screenTitle = when (currentRoute) {
        Routes.SEARCH_MEMBER -> "New Chat"
        Routes.MESSAGE_REQUEST -> "Message Requests"
        else -> "BGC Connect"
    }

    TopAppBar(
        title = {
            Text(
                text = screenTitle,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface // Dynamic text color
            )
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
                    imageVector = Lucide.Dot,
                    contentDescription = "More Actions",
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    )
}

