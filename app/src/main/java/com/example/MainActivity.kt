package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.weight
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ads.AdMobManager
import com.example.model.Skin
import com.example.ui.GameScreen
import com.example.ui.SkinSelectSheet
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val vm: CatGameViewModel by viewModels()
    private lateinit var ads: AdMobManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ads = AdMobManager(this).also { it.init() }

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
                            onFeedFish = { repeat(3) { vm.onCatTapped() } },
                            onChinScratch = { repeat(2) { vm.onCatTapped() } },
                            onWatchAd = {
                                ads.showRewarded(this@MainActivity) {
                                    repeat(5) { vm.onCatTapped() }
                                }
                            }
                        )
                    }
                    BannerAd(ads.bannerId)
                }

                if (showSkins) {
                    SkinSelectSheet(
                        state = state,
                        onSelect = vm::selectSkin,
                        onUnlockWithAd = { skin: Skin ->
                            ads.showRewarded(this@MainActivity) { vm.unlockSkinViaReward(skin) }
                        },
                        onDismiss = { showSkins = false }
                    )
                }

                state.unlockMessage?.let { message ->
                    LaunchedEffect(message) {
                        delay(2_500)
                        vm.consumeUnlockMessage()
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun BannerAd(adUnitId: String) {
    AndroidView(
        modifier = Modifier.fillMaxWidth().height(50.dp),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        },
        update = { it.resume() }
    )
}
