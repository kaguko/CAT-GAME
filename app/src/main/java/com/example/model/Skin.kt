package com.example.model

enum class Skin(
    val id: String,
    val displayName: String,
    val unlockAtPurrCount: Int
) {
    ORANGE("orange", "Cam mập",   0),
    BLACK ("black",  "Đen huyền", 5),
    WHITE ("white",  "Trắng tuyết",15),
    CALICO("calico", "Tam thể",   30);

    companion object {
        fun fromId(id: String?): Skin = entries.firstOrNull { it.id == id } ?: ORANGE
    }
}
