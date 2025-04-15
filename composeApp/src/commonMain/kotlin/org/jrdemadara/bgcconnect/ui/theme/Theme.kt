package org.jrdemadara.bgcconnect.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color

private val lightScheme = lightColorScheme(
    primary = Color(0xFF1A77F2),
    secondary = Color(0xFFF9E5E5),
    tertiary = Color(0xFF1459D7),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF000000),
    surface = Color(0xFFF7FBFE),
    onSurface = Color(0xFF000000),
    onSurfaceVariant = Color(0xFF49454F)
)

private
val darkScheme = darkColorScheme(
    primary = Color(0xFF1A77F2),
    secondary = Color(0xFFF9E5E5),
    tertiary = Color(0xFF1459D7),
    background = Color(0xFF0D0D0B),
    onBackground = Color(0xFFFFFFFF),
    surface = Color(0xFF01050A),
    onSurface = Color(0xFFFFFFFF),
    onSurfaceVariant = Color(0xFFCCCCCC)
)

@Immutable
data class ColorFamily(
    val color: Color,
    val onColor: Color,
    val colorContainer: Color,
    val onColorContainer: Color
)


val unspecified_scheme = ColorFamily(
    Color.Unspecified, Color.Unspecified, Color.Unspecified, Color.Unspecified
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable() () -> Unit
) {
    val colorScheme = if (darkTheme) darkScheme else lightScheme
    //val systemUiController = rememberSystemUiController()
    val statusBarColor = colorScheme.primary // You can change this to any color

    // Update the status bar color
//    SideEffect {
//        systemUiController.setStatusBarColor(color = statusBarColor, darkIcons = !darkTheme)
//    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
