package com.example.mobiledevproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etEmail = findViewById<EditText>(R.id.etLoginEmail)
        val etPassword = findViewById<EditText>(R.id.etLoginPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvGoToRegister = findViewById<TextView>(R.id.tvGoToRegister)

        val sharedPref = getSharedPreferences("UserData", Context.MODE_PRIVATE)

        btnLogin.setOnClickListener{
            val savedEmail = sharedPref.getString("email", "")
            val savedPassword = sharedPref.getString("password", "")
            val enteredEmail = etEmail.text.toString()
            val enteredPassword = etPassword.text.toString()

            if(enteredEmail == savedEmail && enteredPassword == savedPassword){
                sharedPref.edit().putBoolean("loggedIn", true).apply()
                Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }
//        tvGoToRegister.setOnClickListener{
//            startActivity(Intent(this, RegisterActivity::class.java))
//            finish()
//        }

//        testing push. there is problem with illian being able to get this file
    }
}