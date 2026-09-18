package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameUiState
import com.example.ui.theme.Cream
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.WoodDark
import com.example.ui.theme.WoodLight
import com.example.ui.theme.WoodMid

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
        Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(WoodLight, WoodMid, WoodDark)))
    ) {
        Column(
            Modifier.fillMaxSize().padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("🐾 ${state.purrCount}", color = Cream, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                TextButton(onClick = onOpenSkins) {
                    Text("Skins", color = Cream, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(8.dp))
            PurrBar(state.percent, state.state, Modifier.fillMaxWidth())
            Spacer(Modifier.height(4.dp))
            Text("${state.percent.toInt()}% — ${state.state.label}", color = Cream, fontSize = 13.sp)

            Spacer(Modifier.weight(1f))
            Box(Modifier.size(280.dp).pointerInput(Unit) {
                detectTapGestures(onTap = { onCatTapped() })
            }) {
                CatCanvas(state.state, state.currentSkin, state.isClimaxing, Modifier.fillMaxSize())
            }
            Spacer(Modifier.weight(1f))

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                CareButton("🐟 Cho ăn", onFeedFish)
                CareButton("✋ Vuốt cằm", onChinScratch)
                CareButton("🎬 +Thưởng", onWatchAd)
            }
            Spacer(Modifier.height(12.dp))
            state.unlockMessage?.let { message ->
                Surface(color = LeafGreen, shape = RoundedCornerShape(12.dp)) {
                    Text(message, color = Color.White, modifier = Modifier.padding(12.dp))
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
