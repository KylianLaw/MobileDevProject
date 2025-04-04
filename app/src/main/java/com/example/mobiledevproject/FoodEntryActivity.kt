package com.example.mobiledevproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.mobiledevproject.model.FoodEntry

class FoodEntryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_entry)

        val etFoodName = findViewById<EditText>(R.id.etFoodName)
        val etCalories = findViewById<EditText>(R.id.etCalories)
        val btnSave = findViewById<Button>(R.id.btnSaveFood)

        btnSave.setOnClickListener {
            val name = etFoodName.text.toString()
            val calories = etCalories.text.toString().toIntOrNull()

            if (name.isNotBlank() && calories != null) {
                val resultIntent = Intent().apply {
                    putExtra("newFood", FoodEntry(name, calories))
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}
