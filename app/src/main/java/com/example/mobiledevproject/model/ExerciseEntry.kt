package com.example.mobiledevproject.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "exercises")
data class ExerciseEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val caloriesBurned: Int = 0,
    var isChecked: Boolean = false,
    val isUserEntry: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) : Serializable
