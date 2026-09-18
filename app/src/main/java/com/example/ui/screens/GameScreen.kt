package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.viewmodel.CatGameState
import com.example.viewmodel.CatGameViewModel
import kotlinx.coroutines.delay

@Composable
fun GameScreen(viewModel: CatGameViewModel, onOpenRewardedAd: () -> Unit) {
    val state by viewModel.state.collectAsState()

    // Animation for cat reaction on tap
    var tapScale by remember { mutableStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = tapScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "cat_scale"
    )

    // Determine state based on PurrMeter %
    // State 1 (0–40%): Normal eyes wide
    // State 2 (41–80%): Eyes squinting
    // State 3 (81–99%): Eyes half-closed, vibrating
    // State 4 (100%): Blissful
    val catStateTitle = when {
        state.purrMeter >= 100f -> "State 4: Mãn nguyện & Thăng hoa! (100%)"
        state.purrMeter >= 81f -> "State 3: Mắt nhắm hờ, grừ grừ... (81-99%)"
        state.purrMeter >= 41f -> "State 2: Mắt lim dim, đuôi ngoe nguẩy (41-80%)"
        else -> "State 1: Bình thường, mắt mở to (0-40%)"
    }

    val catImageRes = when (state.activeSkin) {
        "black" -> R.drawable.cat_black
        "calico" -> R.drawable.cat_calico
        else -> R.drawable.cat_ginger
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.surface
                    )
                )
            )
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Stats Header
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Số lần đạt đỉnh", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${state.purrCount}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    VerticalDivider(modifier = Modifier.height(32.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Skin hiện tại", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(state.activeSkin.uppercase(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // State Machine Description Badge
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = catStateTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Central Cat Visual (Tap target)
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .scale(animatedScale)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        viewModel.tapCat()
                        tapScale = 1.15f
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = catImageRes),
                    contentDescription = "Turbo Purr Cat",
                    modifier = Modifier
                        .size(220.dp)
                        .clip(CircleShape)
                )

                if (state.isMaxedOut) {
                    // Celebration Overlay effect
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.4f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.Red, modifier = Modifier.size(64.dp))
                            Text(
                                "PURR MAX! ❤️",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Launched Effect to reset scale after tap
            LaunchedEffect(tapScale) {
                if (tapScale > 1f) {
                    delay(100)
                    tapScale = 1f
                }
            }

            // Purr Meter Progress Bar
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("PurrMeter", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text("${state.purrMeter.toInt()}%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { state.purrMeter / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
            }

            // Boost status or Ad reward button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.isBoostActive) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer
                    ) {
                        Text(
                            "⚡ Boost Active: ${state.boostSecondsRemaining}s",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                } else {
                    Button(
                        onClick = { viewModel.watchRewardedAdForBoost() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Xem Ad x2 Tốc độ")
                    }
                }

                Button(
                    onClick = { viewModel.tapCat() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(Icons.Default.TouchApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("VUỐT MÈO (+)")
                }
            }

            // Simulated AdMob Banner at bottom (Chương 4.5 & 6.2)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("AdMob Banner (Test Ad ID)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Quảng cáo tài trợ game", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}
