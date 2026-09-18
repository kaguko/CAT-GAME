package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.GameUiState
import com.example.model.Skin
import com.example.ui.theme.Cream
import com.example.ui.theme.LeafGreen
import com.example.ui.theme.WoodDark

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkinSelectSheet(
    state: GameUiState,
    onSelect: (Skin) -> Unit,
    onUnlockWithAd: (Skin) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Cream
    ) {
        Text(
            "Bộ sưu tập mèo",
            fontSize = 22.sp, fontWeight = FontWeight.Bold,
            color = WoodDark,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.heightIn(max = 500.dp)
        ) {
            items(Skin.entries) { skin ->
                val unlocked = state.isUnlocked(skin)
                val selected = state.currentSkin == skin
                Column(
                    Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (selected) LeafGreen else Color(0xFFE8D9B8))
                        .clickable {
                            if (unlocked) onSelect(skin) else onUnlockWithAd(skin)
                        }
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(if (unlocked) "🐱" else "🔒", fontSize = 42.sp)
                    Spacer(Modifier.height(6.dp))
                    Text(skin.displayName, color = WoodDark, fontWeight = FontWeight.Bold)
                    if (!unlocked) {
                        Text(
                            "Cần ${skin.unlockAtPurrCount} lần",
                            color = WoodDark.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
