package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.viewmodel.CatGameViewModel

data class CatSkin(
    val id: String,
    val name: String,
    val description: String,
    val imageRes: Int,
    val unlockRequirement: String,
    val requiredCount: Int
)

@Composable
fun SkinScreen(viewModel: CatGameViewModel) {
    val state by viewModel.state.collectAsState()

    val skins = listOf(
        CatSkin(
            id = "ginger",
            name = "Classic Ginger",
            description = "Mèo vàng truyền thống, tính cách lười biếng.",
            imageRes = R.drawable.cat_ginger,
            unlockRequirement = "Mặc định",
            requiredCount = 0
        ),
        CatSkin(
            id = "black",
            name = "Shadow Black",
            description = "Mèo đen huyền bí, chuyên đi tuần ban đêm.",
            imageRes = R.drawable.cat_black,
            unlockRequirement = "Đạt đỉnh 3 lần (hoặc xem Ad)",
            requiredCount = 3
        ),
        CatSkin(
            id = "calico",
            name = "Calico Sunshine",
            description = "Mèo tam thể năng động, may mắn.",
            imageRes = R.drawable.cat_calico,
            unlockRequirement = "Đạt đỉnh 6 lần (hoặc xem Ad)",
            requiredCount = 6
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Bộ Sưu Tập Skin Mèo",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Mở khóa skin mới bằng cách tăng điểm PurrMeter hoặc xem quảng cáo thưởng (Rewarded Ad).",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(skins) { skin ->
                val isUnlocked = state.unlockedSkins.contains(skin.id)
                val isActive = state.activeSkin == skin.id

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isActive) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = skin.imageRes),
                            contentDescription = skin.name,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = skin.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = skin.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = skin.unlockRequirement,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        if (isActive) {
                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                Text("Đang chọn", modifier = Modifier.padding(6.dp), color = Color.White)
                            }
                        } else if (isUnlocked) {
                            Button(
                                onClick = { viewModel.selectSkin(skin.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Text("Chọn")
                            }
                        } else {
                            // Can unlock via Rewarded Ad simulation or requirement
                            Button(
                                onClick = { viewModel.watchRewardedAdForUnlock(skin.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Xem Ad Mở Khóa")
                            }
                        }
                    }
                }
            }
        }
    }
}
