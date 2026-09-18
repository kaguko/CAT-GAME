package com.example.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun FloatingHearts(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "hearts")
    val progress by transition.animateFloat(
        0f, 1f, infiniteRepeatable(tween(2200, easing = LinearEasing), RepeatMode.Restart), "progress"
    )
    Canvas(modifier) {
        val radius = minOf(size.width, size.height) * 0.05f
        repeat(8) { index ->
            val phase = (progress + index / 8f) % 1f
            val x = size.width / 2f + sin(phase * 2f * PI.toFloat() + index) * radius * 5f
            val y = size.height / 2f - phase * size.height * 0.55f
            drawHeart(Offset(x, y), radius, Color(0xFFE94B6A).copy(alpha = (1f - phase) * 0.9f))
        }
    }
}

private fun DrawScope.drawHeart(center: Offset, radius: Float, color: Color) {
    val path = Path().apply {
        moveTo(center.x, center.y + radius * 0.9f)
        cubicTo(center.x - radius * 1.6f, center.y - radius * 0.3f, center.x - radius * 0.6f, center.y - radius * 1.4f, center.x, center.y - radius * 0.5f)
        cubicTo(center.x + radius * 0.6f, center.y - radius * 1.4f, center.x + radius * 1.6f, center.y - radius * 0.3f, center.x, center.y + radius * 0.9f)
        close()
    }
    drawPath(path, color)
}
