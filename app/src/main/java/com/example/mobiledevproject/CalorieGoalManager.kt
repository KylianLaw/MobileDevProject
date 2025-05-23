package com.example.mobiledevproject

import android.content.SharedPreferences

class CalorieGoalManager(private val sharedPreferences: SharedPreferences) {
    fun setGoal(goal: Int) {
        sharedPreferences.edit().putInt("dailyGoal", goal).apply()
    }

    fun getGoal(): Int {
        return sharedPreferences.getInt("dailyGoal", 0)
    }
}
