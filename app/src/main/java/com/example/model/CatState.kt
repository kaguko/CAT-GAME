package com.example.model

enum class CatState(val threshold: Float, val label: String) {
    NORMAL(0f, "Mèo tỉnh táo"),
    PURRING(40f, "Mèo lim dim"),
    DEEP_PURR(80f, "Mèo sắp đạt đỉnh"),
    MAX(100f, "Mãn nguyện!");

    companion object {
        fun fromPercent(percent: Float): CatState = when {
            percent >= MAX.threshold -> MAX
            percent >= DEEP_PURR.threshold -> DEEP_PURR
            percent >= PURRING.threshold -> PURRING
            else -> NORMAL
        }
    }
}
