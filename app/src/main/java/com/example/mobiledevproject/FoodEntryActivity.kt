package com.example.mobiledevproject

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mobiledevproject.data.AppDatabase
import com.example.mobiledevproject.model.FoodInput
import kotlinx.coroutines.launch

class FoodEntryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_entry)

        val etFoodName = findViewById<AutoCompleteTextView>(R.id.etFoodName)
        val etCalories = findViewById<EditText>(R.id.etCalories)
        val btnSave = findViewById<Button>(R.id.btnSaveFood)
        val db = AppDatabase.getDatabase(this)

        lifecycleScope.launch {
            val suggestions = db.foodDao().getSuggestions()
            val names = suggestions.map { it.name }
            val adapter = ArrayAdapter(this@FoodEntryActivity, android.R.layout.simple_dropdown_item_1line, names)
            etFoodName.setAdapter(adapter)

            etFoodName.setOnItemClickListener { _, _, position, _ ->
                val selectedName = adapter.getItem(position)
                val selectedFood = suggestions.find { it.name == selectedName }
                selectedFood?.let {
                    etCalories.setText(it.calories.toString())
                }
            }

        }

        btnSave.setOnClickListener {
            val name = etFoodName.text.toString()
            val calories = etCalories.text.toString().toIntOrNull()

            if (name.isNotBlank() && calories != null) {
                val resultIntent = Intent().apply {
                    putExtra("newFood", FoodInput(name, calories))
                }
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
        }
    }
}
