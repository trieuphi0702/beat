package com.example.data.database

import kotlinx.coroutines.flow.Flow

class MixRepository(private val mixDao: MixDao) {

    val allMixes: Flow<List<RecordedMix>> = mixDao.getAllMixes()

    fun getCuePointsForDeck(deckId: String): Flow<List<CuePoint>> = mixDao.getCuePointsForDeck(deckId)

    suspend fun insertMix(mix: RecordedMix): Long = mixDao.insertMix(mix)

    suspend fun deleteMixById(id: Long) = mixDao.deleteMixById(id)

    suspend fun insertCuePoint(cuePoint: CuePoint): Long = mixDao.insertCuePoint(cuePoint)

    suspend fun deleteCuePointById(id: Long) = mixDao.deleteCuePointById(id)
}
