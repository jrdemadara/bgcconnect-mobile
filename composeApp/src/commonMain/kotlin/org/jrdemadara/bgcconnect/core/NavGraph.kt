package org.jrdemadara.bgcconnect.core

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.russhwolf.settings.Settings
import org.jrdemadara.bgcconnect.feature.chat.presentation.ChatScreen
import org.jrdemadara.bgcconnect.feature.home.presentation.HomeScreen
import org.jrdemadara.bgcconnect.feature.login.presentation.LoginScreen
import org.jrdemadara.bgcconnect.feature.chat.features.search_member.presentation.SearchMemberScreen
import org.jrdemadara.bgcconnect.feature.chat.features.thread.presentation.ThreadScreen
import org.jrdemadara.bgcconnect.feature.welcome.presentation.WelcomeScreen
import org.jrdemadara.bgcconnect.ui.components.BottomNavBar
import org.jrdemadara.bgcconnect.ui.components.TopBar
import org.jrdemadara.bgcconnect.ui.components.TopBarSecondary
import org.jrdemadara.bgcconnect.ui.components.TopBarThread
import org.jrdemadara.bgcconnect.ui.icons.HeroPencilSquare


object NavGraph {
    const val AUTH_GRAPH = "auth_graph"
    const val ROOT_GRAPH = "root_graph"
    const val HOME_GRAPH = "home_graph"
    const val CHAT_GRAPH = "chat_graph"
}

object Routes {
    // Auth Routes
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Root Routes
    const val HOME = "home"

    const val CHAT = "chat"
    const val SEARCH_MEMBER = "search_member"
    const val THREAD = "thread"

    const val COMMUNITIES = "communities"
    const val RAFFLE = "raffle"

    const val PROFILE = "profile"
    const val SETTINGS = "settings"
}

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
) {
    val settings: Settings = Settings()
    val isAuthenticated = settings.hasKey("auth_token")

    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) NavGraph.ROOT_GRAPH else NavGraph.AUTH_GRAPH
    ) {
        authNavGraph(navController)
        rootNavGraph(navController)
        chatNavGraph(navController)
    }
}

fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(startDestination = Routes.LOGIN, route = NavGraph.AUTH_GRAPH) {
        composable(Routes.WELCOME) { WelcomeScreen(navController) }
        composable(Routes.LOGIN) { LoginScreen(navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.rootNavGraph(navController: NavHostController) {
    navigation(startDestination = Routes.CHAT, route = NavGraph.ROOT_GRAPH) {
        composable(Routes.HOME) {
            Scaffold(
                topBar = { TopBar(navController) },
                bottomBar = { BottomNavBar(navController) },

                ) { paddingValues ->
                HomeScreen(navController, paddingValues)
            }
        }
        composable(Routes.CHAT) {
            Scaffold(
                topBar = { TopBar(navController) },
                bottomBar = { BottomNavBar(navController) },
                floatingActionButton = {
                    // Floating Action Button
                    FloatingActionButton(
                        onClick = {
                            // Navigate to the new chat screen or show a dialog to create a new chat

                            navController.navigate(NavGraph.CHAT_GRAPH)
                        },
                        modifier = Modifier.padding(16.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = HeroPencilSquare, // Use an appropriate icon
                            contentDescription = "New Chat",
                            tint = Color.White // Icon color
                        )
                    }
                },
            ) { paddingValues ->
                ChatScreen(navController, paddingValues)
            }
        }
        composable(Routes.COMMUNITIES) {
            Scaffold(
                topBar = { TopBar(navController) },
                bottomBar = { BottomNavBar(navController) }
            ) { paddingValues ->
                ChatScreen(navController, paddingValues)
            }
        }
        composable(Routes.RAFFLE) {
            Scaffold(
                topBar = { TopBar(navController) },
                bottomBar = { BottomNavBar(navController) }
            ) { paddingValues ->
                ChatScreen(navController, paddingValues)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.chatNavGraph(navController: NavHostController) {
    navigation(startDestination = Routes.SEARCH_MEMBER, route = NavGraph.CHAT_GRAPH) {
        composable(Routes.SEARCH_MEMBER) {
            Scaffold(
                topBar = { TopBarSecondary(navController) },

                ) { paddingValues ->
                SearchMemberScreen(navController, paddingValues)
            }
        }

        composable(
            Routes.THREAD + "/{id}", // Dynamic argument
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val memberId = backStackEntry.arguments?.getInt("id")
            // Pass the memberId to the screen directly
            ThreadScreen(navController, memberId)
        }
    }
}