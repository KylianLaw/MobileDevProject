package com.example.mobiledevproject.model

import java.io.Serializable

data class FoodEntry(
    val name: String,
    val calories: Int
) : Serializable