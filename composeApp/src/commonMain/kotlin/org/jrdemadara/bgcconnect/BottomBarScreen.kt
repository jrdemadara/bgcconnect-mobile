package org.jrdemadara.bgcconnect

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.composables.icons.lucide.House
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircle
import com.composables.icons.lucide.User
import com.composables.icons.lucide.Users
import org.jrdemadara.bgcconnect.core.Routes
import org.jrdemadara.bgcconnect.ui.icons.HeroChat
import org.jrdemadara.bgcconnect.ui.icons.HeroHome
import org.jrdemadara.bgcconnect.ui.icons.HeroTicket
import org.jrdemadara.bgcconnect.ui.icons.HeroUserGroup

sealed class BottomBarScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomBarScreen(Routes.HOME, "Home",  HeroHome)
    object Chat : BottomBarScreen(Routes.CHAT, "Chat", HeroChat)
    object Communities : BottomBarScreen(Routes.COMMUNITIES, "Communities", HeroUserGroup)
    object Raffle : BottomBarScreen(Routes.RAFFLE, "Raffle", HeroTicket)
}