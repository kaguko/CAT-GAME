package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class CatState {
    Normal, Purring, DeepPurr, Max
}

data class CatGameState(
    val purrMeter: Float = 0f, // 0 to 100
    val purrCount: Int = 0, // Number of times 100% reached
    val activeSkin: String = "ginger", // ginger, black, calico
    val unlockedSkins: Set<String> = setOf("ginger"),
    val isMaxedOut: Boolean = false,
    val isBoostActive: Boolean = false,
    val boostSecondsRemaining: Int = 0,
    val catState: CatState = CatState.Normal
)

class CatGameViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("TurboPurrCatPrefs", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(CatGameState())
    val state: StateFlow<CatGameState> = _state.asStateFlow()

    private var decayJob: Job? = null
    private var boostJob: Job? = null
    private var lastTapTime = System.currentTimeMillis()

    init {
        loadData()
        startDecayLoop()
    }

    private fun getCatState(meter: Float, isMax: Boolean): CatState {
        return when {
            isMax || meter >= 100f -> CatState.Max
            meter >= 81f -> CatState.DeepPurr
            meter >= 41f -> CatState.Purring
            else -> CatState.Normal
        }
    }

    private fun loadData() {
        val count = prefs.getInt("PurrCount", 0)
        val blackUnlocked = prefs.getInt("Skin_Black_Unlocked", 0) == 1
        val calicoUnlocked = prefs.getInt("Skin_Calico_Unlocked", 0) == 1
        val skin = prefs.getString("ActiveSkin", "ginger") ?: "ginger"

        val unlocked = mutableSetOf("ginger")
        if (blackUnlocked) unlocked.add("black")
        if (calicoUnlocked) unlocked.add("calico")

        _state.update {
            it.copy(
                purrCount = count,
                activeSkin = skin,
                unlockedSkins = unlocked,
                catState = getCatState(it.purrMeter, it.isMaxedOut)
            )
        }
    }

    private fun saveData() {
        val editor = prefs.edit()
        editor.putInt("PurrCount", _state.value.purrCount)
        editor.putString("ActiveSkin", _state.value.activeSkin)
        editor.putInt("Skin_Black_Unlocked", if (_state.value.unlockedSkins.contains("black")) 1 else 0)
        editor.putInt("Skin_Calico_Unlocked", if (_state.value.unlockedSkins.contains("calico")) 1 else 0)
        editor.apply()
    }

    fun feedCat() {
        if (_state.value.isMaxedOut) return
        lastTapTime = System.currentTimeMillis()
        _state.update { current ->
            val newMeter = (current.purrMeter + 25f).coerceIn(0f, 100f)
            if (newMeter >= 100f) {
                val newCount = current.purrCount + 1
                val newUnlocked = current.unlockedSkins.toMutableSet()
                if (newCount >= 3) newUnlocked.add("black")
                if (newCount >= 6) newUnlocked.add("calico")

                viewModelScope.launch {
                    saveData()
                    delay(2000)
                    _state.update { it.copy(purrMeter = 0f, isMaxedOut = false, catState = getCatState(0f, false)) }
                }

                current.copy(
                    purrMeter = 100f,
                    purrCount = newCount,
                    unlockedSkins = newUnlocked,
                    isMaxedOut = true,
                    catState = CatState.Max
                )
            } else {
                current.copy(
                    purrMeter = newMeter,
                    catState = getCatState(newMeter, false)
                )
            }
        }
        saveData()
    }

    fun tapCat() {
        if (_state.value.isMaxedOut) return

        lastTapTime = System.currentTimeMillis()
        val increment = if (_state.value.isBoostActive) 16f else 8f
        
        _state.update { current ->
            val newMeter = (current.purrMeter + increment).coerceIn(0f, 100f)
            if (newMeter >= 100f) {
                // Reached peak!
                val newCount = current.purrCount + 1
                // Check unlocks: black unlocked at 3, calico at 6
                val newUnlocked = current.unlockedSkins.toMutableSet()
                if (newCount >= 3) newUnlocked.add("black")
                if (newCount >= 6) newUnlocked.add("calico")

                viewModelScope.launch {
                    saveData()
                    // Wait 2 seconds at 100% then reset
                    delay(2000)
                    _state.update { it.copy(purrMeter = 0f, isMaxedOut = false, catState = getCatState(0f, false)) }
                }

                current.copy(
                    purrMeter = 100f,
                    purrCount = newCount,
                    unlockedSkins = newUnlocked,
                    isMaxedOut = true,
                    catState = CatState.Max
                )
            } else {
                current.copy(
                    purrMeter = newMeter,
                    catState = getCatState(newMeter, false)
                )
            }
        }
        saveData()
    }

    private fun startDecayLoop() {
        decayJob?.cancel()
        decayJob = viewModelScope.launch {
            while (isActive) {
                delay(100) // every 100ms
                val currentState = _state.value
                if (!currentState.isMaxedOut && currentState.purrMeter > 0f) {
                    val timeSinceTap = System.currentTimeMillis() - lastTapTime
                    if (timeSinceTap > 800) { // if idle for 0.8s, start decay
                        val decayRate = if (currentState.purrMeter > 80f) 2.5f else 1.5f
                        _state.update {
                            val newMeter = (it.purrMeter - decayRate).coerceAtLeast(0f)
                            it.copy(
                                purrMeter = newMeter,
                                catState = getCatState(newMeter, it.isMaxedOut)
                            )
                        }
                    }
                }
            }
        }
    }

    fun selectSkin(skin: String) {
        if (_state.value.unlockedSkins.contains(skin)) {
            _state.update { it.copy(activeSkin = skin) }
            saveData()
        }
    }

    fun watchRewardedAdForUnlock(skin: String) {
        // Simulate watching a rewarded ad successfully
        _state.update { current ->
            val newUnlocked = current.unlockedSkins.toMutableSet()
            newUnlocked.add(skin)
            current.copy(unlockedSkins = newUnlocked, activeSkin = skin)
        }
        saveData()
    }

    fun watchRewardedAdForBoost() {
        _state.update { it.copy(isBoostActive = true, boostSecondsRemaining = 30) }
        boostJob?.cancel()
        boostJob = viewModelScope.launch {
            var secs = 30
            while (secs > 0 && isActive) {
                delay(1000)
                secs--
                _state.update { it.copy(boostSecondsRemaining = secs) }
            }
            _state.update { it.copy(isBoostActive = false, boostSecondsRemaining = 0) }
        }
    }
}
