package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.utils.CatSoundPlayer
import com.example.viewmodel.CatGameViewModel
import kotlinx.coroutines.delay

@Composable
fun RetroGameScreen(viewModel: CatGameViewModel) {
    val state by viewModel.state.collectAsState()

    // Animation for cat reaction on tap
    var tapScale by remember { mutableStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = tapScale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "cat_scale"
    )

    // Floating heart trigger state
    var showHeartPopup by remember { mutableStateOf(false) }

    val catStateTitle = when {
        state.purrMeter >= 100f -> "🌸 Mãn nguyện & Thăng hoa (100%)"
        state.purrMeter >= 81f -> "💤 Mắt nhắm hờ, grừ grừ... (81-99%)"
        state.purrMeter >= 41f -> "🐾 Mắt lim dim, đuôi ngoe nguẩy (41-80%)"
        else -> "😺 Bình thường, mắt mở to (0-40%)"
    }

    val catImageRes = when (state.activeSkin) {
        "black" -> R.drawable.cat_black
        "calico" -> R.drawable.cat_calico
        else -> R.drawable.cat_ginger
    }

    // Vintage Ghibli cozy background colors
    val vintageBgBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFFBEB), // Warm cream
            Color(0xFFFEF3C7), // Soft wood
            Color(0xFFFDE68A)  // Vintage amber
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(vintageBgBrush)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Wood-Style Header Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF78350F)), // Rich wood brown
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
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
                        Text("Số Lần Đạt Đỉnh", style = MaterialTheme.typography.labelMedium, color = Color(0xFFFDE68A))
                        Text("${state.purrCount}", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    VerticalDivider(modifier = Modifier.height(32.dp), color = Color(0xFFB45309))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Mèo Đang Nuôi", style = MaterialTheme.typography.labelMedium, color = Color(0xFFFDE68A))
                        Text(state.activeSkin.uppercase(), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // State Badge (Vintage Paper Style)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFEF9C3),
                border = BorderStroke(1.5.dp, Color(0xFFD97706)),
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = catStateTitle,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Central Classic 2D Ghibli Cat Centerpiece (Interactive Touch)
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .scale(animatedScale),
                contentAlignment = Alignment.Center
            ) {
                // Wooden framed circular portrait
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFFD97706))
                        .border(6.dp, Color(0xFF78350F), CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            viewModel.tapCat()
                            CatSoundPlayer.playMeowSound()
                            tapScale = 1.12f
                            showHeartPopup = true
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = catImageRes),
                        contentDescription = "Classic Ghibli Cat",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .clip(CircleShape)
                    )

                    if (state.isMaxedOut) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.Favorite, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(56.dp))
                                Text(
                                    "THĂNG HOA! ❤️",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (showHeartPopup) {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color(0xFFFF4D4D),
                            modifier = Modifier
                                .size(48.dp)
                                .offset(y = (-40).dp)
                        )
                        LaunchedEffect(showHeartPopup) {
                            delay(600)
                            showHeartPopup = false
                        }
                    }
                }
            }

            // Launched Effect to reset tap scale
            LaunchedEffect(tapScale) {
                if (tapScale > 1f) {
                    delay(120)
                    tapScale = 1f
                }
            }

            // Purr Meter Retro Progress Bar
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Độ Thỏa Mãn (PurrMeter)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFF78350F))
                    Text("${state.purrMeter.toInt()}%", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { state.purrMeter / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    color = Color(0xFFD97706),
                    trackColor = Color(0xFFFEF3C7),
                )
            }

            // Functional Buttons Around the Cat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        viewModel.feedCat()
                        CatSoundPlayer.playPurrSound()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(50.dp)
                ) {
                    Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cho Ăn Cá", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        viewModel.tapCat()
                        CatSoundPlayer.playMeowSound()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF78350F)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1.2f).height(50.dp)
                ) {
                    Icon(Icons.Default.Pets, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Vuốt Cằm (+)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            // Rewarded Ad / Boost button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (state.isBoostActive) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFEF08A)
                    ) {
                        Text(
                            "⚡ Boost x2: ${state.boostSecondsRemaining}s",
                            modifier = Modifier.padding(10.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF78350F)
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = { viewModel.watchRewardedAdForBoost() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF78350F)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Xem Ad Tăng Tốc")
                    }
                }

                Text(
                    "MVP Offline-First",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF92400E)
                )
            }
        }
    }
}
