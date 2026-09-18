package com.example.model

data class GameUiState(
    val purr: Float = 0f,
    val purrCount: Int = 0,
    val state: CatState = CatState.NORMAL,
    val currentSkin: Skin = Skin.ORANGE,
    val unlockedSkinIds: Set<String> = setOf(Skin.ORANGE.id),
    val isClimaxing: Boolean = false,
    val unlockMessage: String? = null
) {
    val percent: Float get() = (purr / MAX_PURR * 100f).coerceIn(0f, 100f)

    fun isUnlocked(skin: Skin): Boolean = skin.id in unlockedSkinIds

    companion object {
        const val MAX_PURR = 100f
    }
}
