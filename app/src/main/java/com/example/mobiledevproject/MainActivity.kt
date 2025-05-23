package com.example.mobiledevproject

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
import com.example.mobiledevproject.ExerciseAdapter
import com.example.mobiledevproject.ExerciseSuggestionAdapter
import com.example.mobiledevproject.adapter.FoodAdapter
import com.example.mobiledevproject.data.AppDatabase
import com.example.mobiledevproject.model.ExerciseEntry
import com.example.mobiledevproject.model.FoodEntry
import com.example.mobiledevproject.model.FoodInput
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import android.animation.ObjectAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Notification
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat



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

    private val selectLocationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val lat = result.data?.getDoubleExtra("latitude", 0.0) ?: 0.0
            val lng = result.data?.getDoubleExtra("longitude", 0.0) ?: 0.0
            Toast.makeText(this, "Location selected: ($lat, $lng)", Toast.LENGTH_LONG).show()
        }
    }

    private val addFoodLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val input = result.data?.getSerializableExtra("newFood") as? FoodInput
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }

        createNotificationChannel()

        // Bind UI elements
        tvCalorieGoal = findViewById(R.id.tvCalorieGoal)
        tvTotalCalories = findViewById(R.id.tvTotalCalories)
        tvProgressText = findViewById(R.id.progressText)
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.recyclerViewItems)
        rvExercises = findViewById(R.id.recyclerViewExercises)
        rvExerciseSuggestions = findViewById(R.id.recyclerViewExerciseSuggestions)
        tvExerciseBurned = findViewById(R.id.tvExerciseBurned)
        toggleSuggestionsBtn = findViewById(R.id.btnToggleSuggestions)

        adapter = FoodAdapter(foodList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        exerciseAdapter = ExerciseAdapter(addedExercises.toMutableList()) { exercise ->
            lifecycleScope.launch { db.exerciseDao().update(exercise) }
        }
        rvExercises.layoutManager = LinearLayoutManager(this)
        rvExercises.adapter = exerciseAdapter

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

        findViewById<Button>(R.id.btnOpenMap).setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            selectLocationLauncher.launch(intent)
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

            suggestionAdapter.updateList(db.exerciseDao().getExerciseSuggestions())
        }

        lifecycleScope.launch {
            db.foodDao().getUserFoods().collect { foods ->
                foodList.clear()
                foodList.addAll(foods)
                totalCaloriesConsumed = foodList.sumOf { it.calories }
                adapter.notifyDataSetChanged()
                updateUI()
            }
        }

        lifecycleScope.launch {
            db.exerciseDao().getUserExercises().collect { exercises ->
                addedExercises.clear()
                addedExercises.addAll(exercises)
                totalCaloriesBurned = addedExercises.sumOf { it.caloriesBurned }
                exerciseAdapter.notifyDataSetChanged()
                updateUI()
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

        if (goal > 0 && netCalories >= goal) {
            showGoalReachedNotification()
        }
    }

    private fun showGoalReachedNotification() {
        val channelId = "goal_channel_id"
        val notificationId = 1

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Goal Reached!")
            .setContentText("You’ve reached your calorie goal for today!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(this).notify(notificationId, notification)
        } else {
            Toast.makeText(this, "Notification permission not granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "goal_channel_id",
                "Calorie Goal Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when calorie goal is reached"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}
