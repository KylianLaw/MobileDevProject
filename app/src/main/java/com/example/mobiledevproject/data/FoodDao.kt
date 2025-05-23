package com.example.mobiledevproject.data

import androidx.room.*
import com.example.mobiledevproject.model.FoodEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(food: FoodEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foods: List<FoodEntry>)

    @Query("SELECT COUNT(*) FROM foods")
    suspend fun getCount(): Int

    @Query("SELECT * FROM foods WHERE isUserEntry = 1 ORDER BY timestamp ASC")
    fun getUserFoods(): Flow<List<FoodEntry>>

    @Query("SELECT * FROM foods WHERE isUserEntry = 0 ORDER BY name ASC")
    suspend fun getSuggestions(): List<FoodEntry>
}
