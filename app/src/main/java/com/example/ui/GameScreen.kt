package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameUiState
import com.example.ui.theme.*

@Composable
fun GameScreen(
    state: GameUiState,
    onCatTapped: () -> Unit,
    onOpenSkins: () -> Unit,
    onFeedFish: () -> Unit,
    onChinScratch: () -> Unit,
    onWatchAd: () -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(WoodLight, WoodMid, WoodDark))
            )
    ) {
        Column(
            Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "🐾 ${state.purrCount}",
                    color = Cream,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onOpenSkins) {
                    Text("Skins", color = Cream, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(8.dp))
            PurrBar(state.percent, state.state, Modifier.fillMaxWidth())
            Spacer(Modifier.height(4.dp))
            Text(
                "${state.percent.toInt()}% — ${state.state.label}",
                color = Cream,
                fontSize = 13.sp
            )

            Spacer(Modifier.weight(1f))

            // Mèo
            Box(
                Modifier
                    .size(280.dp)
                    .pointerInput(Unit) { detectTapGestures(onTap = { onCatTapped() }) }
            ) {
                CatCanvas(
                    state = state.state,
                    skin = state.currentSkin,
                    isClimaxing = state.isClimaxing,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.weight(1f))

            // Hành động chăm sóc
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                CareButton("🐟 Cho ăn", onFeedFish)
                CareButton("✋ Vuốt cằm", onChinScratch)
                CareButton("🎬 +Thưởng", onWatchAd)
            }

            Spacer(Modifier.height(12.dp))

            state.unlockMessage?.let {
                Surface(color = LeafGreen, shape = RoundedCornerShape(12.dp)) {
                    Text(it, color = Color.White, modifier = Modifier.padding(12.dp))
                }
            }
        }
    }
}

@Composable
private fun CareButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Cream),
        shape = RoundedCornerShape(14.dp)
    ) {
        Text(label, color = WoodDark, fontWeight = FontWeight.Bold)
    }
}
