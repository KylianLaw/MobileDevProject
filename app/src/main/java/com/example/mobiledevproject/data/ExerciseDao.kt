package com.example.mobiledevproject.data

import androidx.room.*
import com.example.mobiledevproject.model.ExerciseEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: ExerciseEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(exercises: List<ExerciseEntry>)

    @Update
    suspend fun update(exercise: ExerciseEntry)

    @Query("SELECT COUNT(*) FROM exercises")
    suspend fun getCount(): Int

    @Query("SELECT * FROM exercises WHERE isUserEntry = 1 ORDER BY timestamp DESC")
    fun getUserExercises(): Flow<List<ExerciseEntry>>
    @Query("SELECT * FROM exercises WHERE isUserEntry = 0")
    suspend fun getExerciseSuggestions(): List<ExerciseEntry>
}
