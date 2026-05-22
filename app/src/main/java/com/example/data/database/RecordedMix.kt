package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recorded_mixes")
data class RecordedMix(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val filePath: String,
    val durationSeconds: Int,
    val fileSizeBytes: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val bpm: Int = 120
)
