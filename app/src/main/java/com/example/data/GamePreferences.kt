package com.example.data

import android.content.Context
import androidx.core.content.edit
import com.example.model.Skin

class GamePreferences(context: Context) {

    private val prefs = context.getSharedPreferences("turbo_purr_cat", Context.MODE_PRIVATE)

    var purrCount: Int
        get() = prefs.getInt(KEY_PURR_COUNT, 0)
        set(v) = prefs.edit { putInt(KEY_PURR_COUNT, v) }

    var currentSkinId: String
        get() = prefs.getString(KEY_CURRENT_SKIN, Skin.ORANGE.id) ?: Skin.ORANGE.id
        set(v) = prefs.edit { putString(KEY_CURRENT_SKIN, v) }

    fun unlockedSkinIds(): Set<String> {
        val raw = prefs.getStringSet(KEY_UNLOCKED, setOf(Skin.ORANGE.id)) ?: setOf(Skin.ORANGE.id)
        return raw.toSet() + Skin.ORANGE.id
    }

    fun unlockSkin(skin: Skin) {
        val current = unlockedSkinIds().toMutableSet()
        if (current.add(skin.id)) {
            prefs.edit { putStringSet(KEY_UNLOCKED, current) }
        }
    }

    fun resetAll() = prefs.edit { clear() }

    companion object {
        private const val KEY_PURR_COUNT = "purr_count"
        private const val KEY_CURRENT_SKIN = "current_skin"
        private const val KEY_UNLOCKED = "unlocked_skins"
    }
}
