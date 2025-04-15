package org.jrdemadara.bgcconnect.feature.welcome.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import org.jrdemadara.bgcconnect.core.NavGraph
import org.jrdemadara.bgcconnect.core.Routes

@Composable
fun WelcomeScreen(navController: NavController) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("Welcome Screen")

        Button(onClick = {
            navController.navigate(Routes.LOGIN)
        }) { Text("Login") }
    }
}