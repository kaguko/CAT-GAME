package com.example

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.CatSoundPlayer
import com.example.data.GamePreferences
import com.example.model.CatState
import com.example.model.GameUiState
import com.example.model.Skin
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class CatGameViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = GamePreferences(app)
    private val sounds = CatSoundPlayer()
    private val _state = MutableStateFlow(
        GameUiState(
            purrCount = prefs.purrCount,
            currentSkin = Skin.fromId(prefs.currentSkinId),
            unlockedSkinIds = prefs.unlockedSkinIds()
        )
    )
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private var climaxJob: Job? = null
    private var lastTapAt = 0L
    private val tapGain = 4f
    private val decayPerSecond = 6f
    private val graceMs = 800L

    init {
        viewModelScope.launch {
            while (isActive) {
                delay(100L)
                val current = _state.value
                if (!current.isClimaxing && System.currentTimeMillis() - lastTapAt >= graceMs) {
                    val multiplier = 1f + current.state.ordinal * 0.35f
                    applyPurr((current.purr - decayPerSecond * multiplier * 0.1f).coerceAtLeast(0f))
                }
            }
        }
    }

    fun onCatTapped() {
        if (_state.value.isClimaxing) return
        lastTapAt = System.currentTimeMillis()
        sounds.playMeow()
        val next = (_state.value.purr + tapGain).coerceAtMost(GameUiState.MAX_PURR)
        applyPurr(next)
        if (next >= GameUiState.MAX_PURR) triggerClimax()
    }

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

    fun consumeUnlockMessage() = _state.update { it.copy(unlockMessage = null) }

    private fun applyPurr(purr: Float) {
        _state.update { it.copy(purr = purr, state = CatState.fromPercent(purr)) }
        if (_state.value.state >= CatState.DEEP_PURR) sounds.startPurr() else sounds.stopPurr()
    }

    private fun triggerClimax() {
        if (_state.value.isClimaxing) return
        climaxJob?.cancel()
        climaxJob = viewModelScope.launch {
            sounds.stopPurr()
            _state.update { it.copy(isClimaxing = true, state = CatState.MAX, purr = GameUiState.MAX_PURR) }
            val count = _state.value.purrCount + 1
            prefs.purrCount = count
            var updated = _state.value.copy(purrCount = count)
            Skin.entries.firstOrNull { it.unlockAtPurrCount <= count && it.id !in updated.unlockedSkinIds }
                ?.let { skin ->
                    prefs.unlockSkin(skin)
                    updated = updated.copy(
                        unlockedSkinIds = updated.unlockedSkinIds + skin.id,
                        unlockMessage = "Mở khóa skin: ${skin.displayName}!"
                    )
                }
            _state.value = updated
            delay(2_000L)
            _state.update { it.copy(purr = 0f, state = CatState.NORMAL, isClimaxing = false) }
        }
    }

    override fun onCleared() {
        climaxJob?.cancel()
        sounds.release()
        super.onCleared()
    }
}
