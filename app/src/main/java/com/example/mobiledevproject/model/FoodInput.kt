package com.example.mobiledevproject.model

import java.io.Serializable

data class FoodInput(
    val name: String,
    var calories: Int
) : Serializable