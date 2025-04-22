package org.jrdemadara.bgcconnect.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
fun WavyTypingText(
    text: String = "Typing...",
    color: Color = Color.Gray,
    style: TextStyle = MaterialTheme.typography.labelLarge
) {
    val infiniteTransition = rememberInfiniteTransition()
    val animatedOffsets = text.mapIndexed { index, _ ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 400, delayMillis = index * 100, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
    }

    Row(verticalAlignment = Alignment.Bottom) {
        text.forEachIndexed { index, char ->
            val offsetY = animatedOffsets[index].value * -6f // move up to 6dp
            Text(
                text = char.toString(),
                modifier = Modifier
                    .offset(y = offsetY.dp),
                color = color,
                style = style
            )
        }
    }
}