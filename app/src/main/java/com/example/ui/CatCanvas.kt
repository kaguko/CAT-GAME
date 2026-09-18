package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.model.CatState
import com.example.model.Skin
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun CatCanvas(
    state: CatState,
    skin: Skin,
    isClimaxing: Boolean,
    modifier: Modifier = Modifier
) {
    val infinite = rememberInfiniteTransition(label = "cat")
    val breath by infinite.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Reverse),
        label = "breath"
    )

    val base = remember(skin) {
        when (skin) {
            Skin.ORANGE -> Color(0xFFE8A15A)
            Skin.BLACK  -> Color(0xFF3A3A3A)
            Skin.WHITE  -> Color(0xFFF2EFE6)
            Skin.CALICO -> Color(0xFFD9B38C)
        }
    }
    val earInner = when (skin) {
        Skin.ORANGE -> Color(0xFFF4C79A)
        Skin.BLACK  -> Color(0xFF5A5A5A)
        Skin.WHITE  -> Color(0xFFF9C4C4)
        Skin.CALICO -> Color(0xFFB26A47)
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h / 2f
        val faceR = minOf(w, h) * 0.32f

        // ----- Tai -----
        fun drawEar(mirrorX: Float) {
            val path = Path().apply {
                moveTo(cx + mirrorX * faceR * 0.85f, cy - faceR * 0.45f)
                lineTo(cx + mirrorX * faceR * 1.05f, cy - faceR * 1.25f)
                lineTo(cx + mirrorX * faceR * 0.35f, cy - faceR * 0.85f)
                close()
            }
            drawPath(path, base)
            val inner = Path().apply {
                moveTo(cx + mirrorX * faceR * 0.78f, cy - faceR * 0.55f)
                lineTo(cx + mirrorX * faceR * 0.98f, cy - faceR * 1.05f)
                lineTo(cx + mirrorX * faceR * 0.5f, cy - faceR * 0.8f)
                close()
            }
            drawPath(inner, earInner)
        }
        drawEar(-1f); drawEar(1f)

        // ----- Đầu (hơi phồng theo nhịp thở) -----
        val breathScale = 1f + 0.02f * breath
        drawOval(
            color = base,
            topLeft = Offset(cx - faceR * breathScale, cy - faceR * 0.95f * breathScale),
            size = Size(faceR * 2f * breathScale, faceR * 2.05f * breathScale)
        )

        // ----- Mắt theo state -----
        val eyeY = cy - faceR * 0.1f
        val eyeXOff = faceR * 0.45f
        val eyeColor = Color(0xFF241A12)

        fun openEye(x: Float) {
            drawOval(eyeColor, Offset(x - faceR * 0.11f, eyeY - faceR * 0.18f),
                Size(faceR * 0.22f, faceR * 0.36f))
            drawOval(Color.White, Offset(x - faceR * 0.04f, eyeY - faceR * 0.12f),
                Size(faceR * 0.08f, faceR * 0.12f))
        }
        fun lidEye(x: Float) {
            drawArc(eyeColor,
                startAngle = 180f, sweepAngle = 180f, useCenter = false,
                topLeft = Offset(x - faceR * 0.16f, eyeY - faceR * 0.12f),
                size = Size(faceR * 0.32f, faceR * 0.32f),
                style = Stroke(width = faceR * 0.08f))
        }
        fun closedEye(x: Float) {
            drawArc(eyeColor,
                startAngle = 0f, sweepAngle = 180f, useCenter = false,
                topLeft = Offset(x - faceR * 0.16f, eyeY - faceR * 0.02f),
                size = Size(faceR * 0.32f, faceR * 0.24f),
                style = Stroke(width = faceR * 0.08f))
        }

        when (state) {
            CatState.NORMAL   -> { openEye(cx - eyeXOff); openEye(cx + eyeXOff) }
            CatState.PURRING  -> { lidEye(cx - eyeXOff);  lidEye(cx + eyeXOff) }
            CatState.DEEP_PURR-> { closedEye(cx - eyeXOff); closedEye(cx + eyeXOff) }
            CatState.MAX      -> { closedEye(cx - eyeXOff); closedEye(cx + eyeXOff) }
        }

        // ----- Mũi -----
        val nose = Path().apply {
            moveTo(cx, cy + faceR * 0.15f)
            lineTo(cx - faceR * 0.08f, cy + faceR * 0.02f)
            lineTo(cx + faceR * 0.08f, cy + faceR * 0.02f)
            close()
        }
        drawPath(nose, Color(0xFFE27C7C))

        // ----- Miệng: cười khi MAX / nhíu khi bình thường -----
        val mouthPath = Path().apply {
            val my = cy + faceR * 0.32f
            if (state == CatState.MAX) {
                moveTo(cx - faceR * 0.2f, my)
                quadraticBezierTo(cx, my + faceR * 0.28f, cx + faceR * 0.2f, my)
            } else {
                moveTo(cx - faceR * 0.15f, my)
                quadraticBezierTo(cx - faceR * 0.07f, my + faceR * 0.12f, cx, my)
                quadraticBezierTo(cx + faceR * 0.07f, my + faceR * 0.12f, cx + faceR * 0.15f, my)
            }
        }
        drawPath(mouthPath, eyeColor, style = Stroke(width = faceR * 0.05f))

        // ----- Râu -----
        val whiskerAlpha = if (state == CatState.DEEP_PURR || state == CatState.MAX) 0.35f else 0.7f
        repeat(3) { i ->
            val dy = (i - 1) * faceR * 0.12f
            drawLine(eyeColor.copy(alpha = whiskerAlpha),
                Offset(cx - faceR * 0.7f, cy + faceR * 0.15f + dy),
                Offset(cx - faceR * 1.25f, cy + faceR * 0.1f + dy * 1.5f),
                strokeWidth = faceR * 0.025f)
            drawLine(eyeColor.copy(alpha = whiskerAlpha),
                Offset(cx + faceR * 0.7f, cy + faceR * 0.15f + dy),
                Offset(cx + faceR * 1.25f, cy + faceR * 0.1f + dy * 1.5f),
                strokeWidth = faceR * 0.025f)
        }

        // ----- Tim bay lên khi MAX -----
        if (isClimaxing) {
            repeat(6) { i ->
                val phase = (breath + i / 6f) % 1f
                val hx = cx + sin(phase * 2f * PI.toFloat() + i) * faceR * 0.9f
                val hy = cy - faceR * 0.5f - phase * faceR * 2.2f
                val a = (1f - phase).coerceIn(0f, 1f)
                drawHeart(Offset(hx, hy), faceR * 0.14f, Color(0xFFE94B6A).copy(alpha = a))
            }
        }
    }
}

private fun DrawScope.drawHeart(
    center: Offset, r: Float, color: Color
) {
    val path = Path().apply {
        moveTo(center.x, center.y + r * 0.9f)
        cubicTo(center.x - r * 1.6f, center.y - r * 0.3f, center.x - r * 0.6f, center.y - r * 1.4f, center.x, center.y - r * 0.5f)
        cubicTo(center.x + r * 0.6f, center.y - r * 1.4f, center.x + r * 1.6f, center.y - r * 0.3f, center.x, center.y + r * 0.9f)
        close()
    }
    drawPath(path, color)
}
