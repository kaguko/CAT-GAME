package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.CatSoundPlayer
import com.example.data.GamePreferences
import com.example.model.CatState
import com.example.model.GameUiState
import com.example.model.Skin
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class CatGameViewModel(app: Application) : AndroidViewModel(app) {

    private val prefs = GamePreferences(app)
    val sounds = CatSoundPlayer()

    private val _state = MutableStateFlow(
        GameUiState(
            purrCount = prefs.purrCount,
            currentSkin = Skin.fromId(prefs.currentSkinId),
            unlockedSkinIds = prefs.unlockedSkinIds()
        )
    )
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private var decayJob: Job? = null
    private var climaxJob: Job? = null
    private var lastTapAt = 0L

    // Cấu hình cân bằng
    private val tapGain = 4f
    private val decayPerSecond = 6f
    private val graceMs = 800L

    init { startDecayLoop() }

    // ---------- INPUT ----------
    fun onCatTapped() {
        if (_state.value.isClimaxing) return

        lastTapAt = System.currentTimeMillis()
        sounds.playMeow()

        val newPurr = (_state.value.purr + tapGain).coerceAtMost(GameUiState.MAX_PURR)
        applyPurr(newPurr)

        if (newPurr >= GameUiState.MAX_PURR) triggerClimax()
    }

    // ---------- DECAY LOOP ----------
    private fun startDecayLoop() {
        decayJob?.cancel()
        decayJob = viewModelScope.launch {
            while (isActive) {
                delay(100L)  // 10Hz — đủ mượt và tiết kiệm pin

                val s = _state.value
                if (s.isClimaxing) continue
                if (System.currentTimeMillis() - lastTapAt < graceMs) continue

                // Decay mạnh dần theo state
                val multiplier = 1f + s.state.ordinal * 0.35f
                val drop = decayPerSecond * multiplier * 0.1f
                applyPurr((s.purr - drop).coerceAtLeast(0f))
            }
        }
    }

    private fun applyPurr(newPurr: Float) {
        _state.update { old ->
            val newState = CatState.fromPercent(newPurr / GameUiState.MAX_PURR * 100f)
            old.copy(purr = newPurr, state = newState)
        }
        // Điều khiển tiếng purr loop
        if (_state.value.state >= CatState.DEEP_PURR) sounds.startPurr()
        else sounds.stopPurr()
    }

    // ---------- CLIMAX ----------
    private fun triggerClimax() {
        if (_state.value.isClimaxing) return
        climaxJob?.cancel()
        climaxJob = viewModelScope.launch {
            sounds.stopPurr()
            _state.update { it.copy(isClimaxing = true, state = CatState.MAX, purr = GameUiState.MAX_PURR) }

            // Tăng counter + kiểm tra unlock
            val newCount = _state.value.purrCount + 1
            prefs.purrCount = newCount

            var updated = _state.value.copy(purrCount = newCount)
            val newlyUnlocked = Skin.entries.firstOrNull {
                it.unlockAtPurrCount <= newCount && it.id !in updated.unlockedSkinIds
            }
            if (newlyUnlocked != null) {
                prefs.unlockSkin(newlyUnlocked)
                updated = updated.copy(
                    unlockedSkinIds = updated.unlockedSkinIds + newlyUnlocked.id,
                    unlockMessage = "Mở khóa skin: ${newlyUnlocked.displayName}!"
                )
            }
            _state.value = updated

            delay(2000L)

            _state.update { it.copy(purr = 0f, state = CatState.NORMAL, isClimaxing = false) }
        }
    }

    // ---------- SKIN ----------
    fun selectSkin(skin: Skin) {
        if (!_state.value.isUnlocked(skin)) return
        prefs.currentSkinId = skin.id
        _state.update { it.copy(currentSkin = skin) }
    }

    fun unlockSkinViaReward(skin: Skin) {
        prefs.unlockSkin(skin)
        _state.update { it.copy(unlockedSkinIds = it.unlockedSkinIds + skin.id) }
        selectSkin(skin)
    }

    fun consumeUnlockMessage() {
        _state.update { it.copy(unlockMessage = null) }
    }

    override fun onCleared() {
        decayJob?.cancel()
        climaxJob?.cancel()
        sounds.release()
        super.onCleared()
    }
}
