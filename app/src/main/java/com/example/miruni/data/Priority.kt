package com.example.miruni.data

enum class Priority(val localLabel: String) {
    HIGH("상"),
    MEDIUM("중"),
    LOW("하");

    companion object {
        fun fromLocalLabel(label: String): Priority? =
            values().find { it.localLabel == label }
    }
}