package com.typingarena.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.typingarena.data.model.TypingResult
import kotlinx.coroutines.flow.Flow

@Dao
interface TypingResultDao {

    @Insert
    suspend fun insertResult(result: TypingResult): Long

    @Query("SELECT * FROM typing_results ORDER BY timestamp DESC")
    fun getAllResults(): Flow<List<TypingResult>>

    @Query("SELECT MAX(wpm) FROM typing_results")
    fun getHighestWpm(): Flow<Int?>

    @Query("SELECT AVG(wpm) FROM typing_results")
    fun getAverageWpm(): Flow<Float?>

    @Query("SELECT AVG(accuracy) FROM typing_results")
    fun getAverageAccuracy(): Flow<Float?>

    @Query("SELECT COUNT(*) FROM typing_results")
    fun getTotalTestsCompleted(): Flow<Int>

    @Query("SELECT SUM(durationSeconds) FROM typing_results")
    fun getTotalTimePracticedSeconds(): Flow<Long?>

    @Query("DELETE FROM typing_results")
    suspend fun clearAllResults()
}
