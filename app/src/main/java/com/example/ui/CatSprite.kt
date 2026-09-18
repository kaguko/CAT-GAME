package com.example.ui

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.model.CatState
import com.example.model.Skin

/** Displays a pre-rendered transparent sprite; Compose only performs GPU scaling. */
@Composable
fun CatSprite(
    state: CatState,
    skin: Skin,
    isClimaxing: Boolean,
    modifier: Modifier = Modifier
) {
    val resId = remember(skin, state) { spriteRes(skin, state) }
    val transition = rememberInfiniteTransition(label = "catBreath")
    val breath by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breath"
    )
    val climaxScale by animateFloatAsState(
        targetValue = if (isClimaxing) 1.08f else 1f,
        animationSpec = spring(dampingRatio = 0.4f, stiffness = 400f),
        label = "climaxPop"
    )

    Box(modifier, contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(resId),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize().scale((1f + breath * 0.02f) * climaxScale)
        )
        if (isClimaxing) FloatingHearts(Modifier.fillMaxSize())
    }
}

@DrawableRes
private fun spriteRes(skin: Skin, state: CatState): Int = when (skin) {
    Skin.ORANGE -> when (state) {
        CatState.NORMAL -> R.drawable.cat_orange_normal
        CatState.PURRING -> R.drawable.cat_orange_purring
        CatState.DEEP_PURR -> R.drawable.cat_orange_deep_purr
        CatState.MAX -> R.drawable.cat_orange_max
    }
    Skin.BLACK -> when (state) {
        CatState.NORMAL -> R.drawable.cat_black_normal
        CatState.PURRING -> R.drawable.cat_black_purring
        CatState.DEEP_PURR -> R.drawable.cat_black_deep_purr
        CatState.MAX -> R.drawable.cat_black_max
    }
    Skin.WHITE -> when (state) {
        CatState.NORMAL -> R.drawable.cat_white_normal
        CatState.PURRING -> R.drawable.cat_white_purring
        CatState.DEEP_PURR -> R.drawable.cat_white_deep_purr
        CatState.MAX -> R.drawable.cat_white_max
    }
    Skin.CALICO -> when (state) {
        CatState.NORMAL -> R.drawable.cat_calico_normal
        CatState.PURRING -> R.drawable.cat_calico_purring
        CatState.DEEP_PURR -> R.drawable.cat_calico_deep_purr
        CatState.MAX -> R.drawable.cat_calico_max
    }
}
