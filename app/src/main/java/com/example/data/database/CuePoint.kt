package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cue_points")
data class CuePoint(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val deckId: String, // "DECK_A" or "DECK_B"
    val positionLabel: String, // e.g., "Intro", "Verse", "Drop"
    val positionPercent: Float, // 0.0 to 1.0 position on vinyl
    val colorHex: String, // Visual indicator on turntable
    val timestamp: Long = System.currentTimeMillis()
)
