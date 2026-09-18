package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ads.AdMobManager
import com.example.model.Skin
import com.example.ui.GameScreen
import com.example.ui.SkinSelectSheet

class MainActivity : ComponentActivity() {

    private val vm: CatGameViewModel by viewModels()
    private lateinit var ads: AdMobManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ads = AdMobManager(this)
        ads.init { /* sẵn sàng */ }

        setContent {
            val state by vm.state.collectAsStateWithLifecycle()
            var showSkins by remember { mutableStateOf(false) }

            Surface(Modifier.fillMaxSize()) {
                Column(Modifier.fillMaxSize()) {
                    Box(Modifier.weight(1f)) {
                        GameScreen(
                            state = state,
                            onCatTapped = vm::onCatTapped,
                            onOpenSkins = { showSkins = true },
                            onFeedFish  = { repeat(3) { vm.onCatTapped() } },
                            onChinScratch = { repeat(2) { vm.onCatTapped() } },
                            onWatchAd = {
                                ads.showRewarded(this@MainActivity) {
                                    // Thưởng: +20 purr
                                    repeat(5) { vm.onCatTapped() }
                                }
                            }
                        )
                    }

                    // Banner Ad ở đáy (AdMob Banner Container)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(Color(0xFF1E1E1E))
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF2C2C2C),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = RoundedCornerShape(2.dp),
                                        color = Color(0xFFFBBC05),
                                        modifier = Modifier.padding(end = 8.dp)
                                    ) {
                                        Text(
                                            "Ad",
                                            color = Color.Black,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                    Text(
                                        "Google AdMob • Test Banner 320x50",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Text(
                                    "ca-app-pub-3940...6300978111",
                                    color = Color(0xFF9E9E9E),
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }

                if (showSkins) {
                    SkinSelectSheet(
                        state = state,
                        onSelect = { vm.selectSkin(it) },
                        onUnlockWithAd = { skin: Skin ->
                            ads.showRewarded(this@MainActivity) {
                                vm.unlockSkinViaReward(skin)
                            }
                        },
                        onDismiss = { showSkins = false }
                    )
                }

                state.unlockMessage?.let {
                    LaunchedEffect(it) {
                        kotlinx.coroutines.delay(2500)
                        vm.consumeUnlockMessage()
                    }
                }
            }
        }
    }
}
