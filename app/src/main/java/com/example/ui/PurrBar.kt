package com.example.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.model.CatState
import com.example.ui.theme.Cream
import com.example.ui.theme.PurrOrange
import com.example.ui.theme.PurrPink

@Composable
fun PurrBar(percent: Float, state: CatState, modifier: Modifier = Modifier) {
    val target = (percent / 100f).coerceIn(0f, 1f)
    val animated by animateFloatAsState(target, label = "purrBar")
    val color by animateColorAsState(
        when (state) {
            CatState.NORMAL    -> PurrOrange
            CatState.PURRING   -> PurrPink
            CatState.DEEP_PURR -> Color(0xFFE94B6A)
            CatState.MAX       -> Color(0xFFFFD166)
        }, label = "purrColor"
    )

    Box(
        modifier = modifier
            .height(22.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(Cream)
            .padding(3.dp)
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(animated)
                .clip(RoundedCornerShape(9.dp))
                .background(Brush.horizontalGradient(listOf(color, color.copy(alpha = 0.7f))))
        )
    }
}
