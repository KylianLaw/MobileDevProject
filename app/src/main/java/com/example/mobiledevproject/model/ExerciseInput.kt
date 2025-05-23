package com.example.mobiledevproject.model

import java.io.Serializable

data class ExerciseInput(
    val name: String,
    val caloriesBurned: Int = 0
) : Serializable