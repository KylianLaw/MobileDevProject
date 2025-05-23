package com.example.mobiledevproject

import android.content.SharedPreferences

class LoginManager(private val sharedPreferences: SharedPreferences) {
    fun login(email: String, password: String): Boolean {
        val savedEmail = sharedPreferences.getString("email", null)
        val savedPassword = sharedPreferences.getString("password", null)
        return email == savedEmail && password == savedPassword
    }
}
