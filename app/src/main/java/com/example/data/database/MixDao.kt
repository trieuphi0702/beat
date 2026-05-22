package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MixDao {

    @Query("SELECT * FROM recorded_mixes ORDER BY timestamp DESC")
    fun getAllMixes(): Flow<List<RecordedMix>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMix(mix: RecordedMix): Long

    @Query("DELETE FROM recorded_mixes WHERE id = :id")
    suspend fun deleteMixById(id: Long)

    @Query("SELECT * FROM cue_points WHERE deckId = :deckId ORDER BY timestamp ASC")
    fun getCuePointsForDeck(deckId: String): Flow<List<CuePoint>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCuePoint(cuePoint: CuePoint): Long

    @Query("DELETE FROM cue_points WHERE id = :id")
    suspend fun deleteCuePointById(id: Long)
}
