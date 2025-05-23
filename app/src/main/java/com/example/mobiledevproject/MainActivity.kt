package com.example.mobiledevproject

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobiledevproject.adapter.FoodAdapter
import com.example.mobiledevproject.data.AppDatabase
import com.example.mobiledevproject.model.ExerciseEntry
import com.example.mobiledevproject.model.FoodEntry
import com.example.mobiledevproject.model.FoodInput
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FoodAdapter
    private lateinit var tvTotalCalories: TextView
    private lateinit var tvCalorieGoal: TextView
    private lateinit var tvProgressText: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var tvExerciseBurned: TextView
    private lateinit var rvExercises: RecyclerView
    private lateinit var exerciseAdapter: ExerciseAdapter

    private lateinit var rvExerciseSuggestions: RecyclerView
    private lateinit var suggestionAdapter: ExerciseSuggestionAdapter
    private lateinit var toggleSuggestionsBtn: Button

    private val foodList = mutableListOf<FoodEntry>()
    private val addedExercises = mutableListOf<ExerciseEntry>()

    private var totalCaloriesConsumed = 0
    private var totalCaloriesBurned = 0

    private val db by lazy { AppDatabase.getDatabase(this) }

    private val addFoodLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val input = data?.getSerializableExtra("newFood") as? FoodInput
            input?.let {
                val newFood = FoodEntry(name = it.name, calories = it.calories, isUserEntry = true)
                lifecycleScope.launch { db.foodDao().insert(newFood) }
                foodList.add(newFood)
                totalCaloriesConsumed += newFood.calories
                updateUI()
                adapter.notifyItemInserted(foodList.size - 1)
            }
        }
    }

    private fun updateUI() {
        val prefs = getSharedPreferences("UserData", Context.MODE_PRIVATE)
        val goal = prefs.getInt("dailyGoal", 0)
        val netCalories = (totalCaloriesConsumed - totalCaloriesBurned).coerceAtLeast(0)

        tvTotalCalories.text = "Total Calories: $netCalories"
        tvExerciseBurned.text = "Calories Burned: $totalCaloriesBurned"

        val progress = if (goal > 0) (netCalories * 100 / goal).coerceAtMost(100) else 0
        tvProgressText.text = "$progress%"

        ObjectAnimator.ofInt(progressBar, "progress", progressBar.progress, progress).apply {
            duration = 600
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // UI Bindings
        tvCalorieGoal = findViewById(R.id.tvCalorieGoal)
        tvTotalCalories = findViewById(R.id.tvTotalCalories)
        tvProgressText = findViewById(R.id.progressText)
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerViewItems)
        rvExercises = findViewById(R.id.recyclerViewExercises)
        rvExerciseSuggestions = findViewById(R.id.recyclerViewExerciseSuggestions)
        tvExerciseBurned = findViewById(R.id.tvExerciseBurned)
        toggleSuggestionsBtn = findViewById(R.id.btnToggleSuggestions)

        // Food List
        adapter = FoodAdapter(foodList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Exercise Log List
        exerciseAdapter = ExerciseAdapter(addedExercises.toMutableList()) { exercise ->
            lifecycleScope.launch { db.exerciseDao().update(exercise) }
        }
        rvExercises.layoutManager = LinearLayoutManager(this)
        rvExercises.adapter = exerciseAdapter

        // Exercise Suggestions List
        suggestionAdapter = ExerciseSuggestionAdapter(mutableListOf()) { exercise ->
            val newExercise = exercise.copy(isUserEntry = true)
            lifecycleScope.launch { db.exerciseDao().insert(newExercise) }
            addedExercises.add(newExercise)
            totalCaloriesBurned += newExercise.caloriesBurned
            updateUI()
            exerciseAdapter.notifyItemInserted(addedExercises.size - 1)
        }
        rvExerciseSuggestions.layoutManager = LinearLayoutManager(this)
        rvExerciseSuggestions.adapter = suggestionAdapter

        toggleSuggestionsBtn.setOnClickListener {
            rvExerciseSuggestions.visibility =
                if (rvExerciseSuggestions.visibility == View.GONE) View.VISIBLE else View.GONE
        }

        findViewById<Button>(R.id.btnSetGoal).setOnClickListener {
            val input = EditText(this).apply { inputType = InputType.TYPE_CLASS_NUMBER }
            AlertDialog.Builder(this)
                .setTitle("Set Daily Calorie Goal")
                .setView(input)
                .setPositiveButton("Save") { _, _ ->
                    val goal = input.text.toString().toIntOrNull() ?: 0
                    getSharedPreferences("UserData", Context.MODE_PRIVATE)
                        .edit().putInt("dailyGoal", goal).apply()
                    tvCalorieGoal.text = "Goal: $goal cal"
                    updateUI()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        findViewById<FloatingActionButton>(R.id.fabAddFood).setOnClickListener {
            addFoodLauncher.launch(Intent(this, FoodEntryActivity::class.java))
        }

        // Insert sample data on first launch
        lifecycleScope.launch {
            if (db.foodDao().getCount() == 0) {
                db.foodDao().insertAll(
                    listOf(
                        FoodEntry(name = "Apple", calories = 95),
                        FoodEntry(name = "Banana", calories = 105),
                        FoodEntry(name = "Orange", calories = 62),
                        FoodEntry(name = "Egg", calories = 78),
                        FoodEntry(name = "Bread (1 slice)", calories = 79),
                        FoodEntry(name = "Rice (1 cup)", calories = 206),
                        FoodEntry(name = "Grilled Chicken Breast", calories = 165),
                        FoodEntry(name = "French Fries", calories = 365),
                        FoodEntry(name = "Pizza Slice", calories = 285),
                        FoodEntry(name = "Burger", calories = 354),
                        FoodEntry(name = "Spaghetti", calories = 221),
                        FoodEntry(name = "Tuna (can)", calories = 132),
                        FoodEntry(name = "Ice Cream", calories = 137),
                        FoodEntry(name = "Protein Shake", calories = 250),
                        FoodEntry(name = "Pancakes", calories = 175),
                        FoodEntry(name = "Waffles", calories = 218),
                        FoodEntry(name = "Coke", calories = 140),
                        FoodEntry(name = "Chocolate Bar", calories = 229),
                        FoodEntry(name = "Croissant", calories = 231),
                        FoodEntry(name = "Oatmeal", calories = 150),
                        FoodEntry(name = "Malick (Muscle Mass)", calories = 10000)
                    )
                )
            }

            if (db.exerciseDao().getCount() == 0) {
                db.exerciseDao().insertAll(
                    listOf(
                        ExerciseEntry(name = "Walking (30 mins)", caloriesBurned = 100),
                        ExerciseEntry(name = "Running (30 mins)", caloriesBurned = 300),
                        ExerciseEntry(name = "Cycling (30 mins)", caloriesBurned = 250),
                        ExerciseEntry(name = "Swimming (30 mins)", caloriesBurned = 350),
                        ExerciseEntry(name = "Jump Rope", caloriesBurned = 200),
                        ExerciseEntry(name = "Yoga", caloriesBurned = 180),
                        ExerciseEntry(name = "Weightlifting", caloriesBurned = 170),
                        ExerciseEntry(name = "HIT", caloriesBurned = 250),
                        ExerciseEntry(name = "Elliptical", caloriesBurned = 280),
                        ExerciseEntry(name = "Stair Climbing", caloriesBurned = 220),
                        ExerciseEntry(name = "Dancing", caloriesBurned = 200),
                        ExerciseEntry(name = "Push-ups", caloriesBurned = 75),
                        ExerciseEntry(name = "Sit-ups", caloriesBurned = 60),
                        ExerciseEntry(name = "Hiking", caloriesBurned = 430),
                        ExerciseEntry(name = "Basketball", caloriesBurned = 270),
                        ExerciseEntry(name = "Soccer", caloriesBurned = 300),
                        ExerciseEntry(name = "Boxing", caloriesBurned = 350),
                        ExerciseEntry(name = "Pilates", caloriesBurned = 150)
                    )
                )
            }

            // Update suggestions list
            val suggestions = db.exerciseDao().getExerciseSuggestions()
            suggestionAdapter.updateList(suggestions)
        }

        // Load food entries
        lifecycleScope.launch {
            db.foodDao().getUserFoods().collect { foods ->
                foodList.clear()
                foodList.addAll(foods)
                totalCaloriesConsumed = foodList.sumOf { it.calories }
                adapter.notifyDataSetChanged()
                updateUI()
            }
        }

        // Load added exercises
        lifecycleScope.launch {
            db.exerciseDao().getUserExercises().collect { userExercises ->
                addedExercises.clear()
                addedExercises.addAll(userExercises)
                totalCaloriesBurned = addedExercises.sumOf { it.caloriesBurned }
                exerciseAdapter.notifyDataSetChanged()
                updateUI()
            }
        }
    }
}
